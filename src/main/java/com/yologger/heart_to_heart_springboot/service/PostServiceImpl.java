package com.yologger.heart_to_heart_springboot.service;

import com.yologger.heart_to_heart_springboot.controller.api.v1.post.dto.RegisterPostRequestDTO;
import com.yologger.heart_to_heart_springboot.controller.service.PostService;
import com.yologger.heart_to_heart_springboot.repository.MemberRepository;
import com.yologger.heart_to_heart_springboot.repository.PostRepository;
import com.yologger.heart_to_heart_springboot.repository.entity.MemberEntity;
import com.yologger.heart_to_heart_springboot.repository.entity.PostEntity;
import com.yologger.heart_to_heart_springboot.repository.entity.PostImageEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.yologger.heart_to_heart_springboot.util.AwsS3Uploader;

import javax.persistence.EntityNotFoundException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class PostServiceImpl implements PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final AwsS3Uploader awsS3Uploader;

    @Override
    @Transactional
    public ResponseEntity<JSONObject> registerPost(RegisterPostRequestDTO request) {

        Long userId = request.getUserId();
        String content = request.getContent();

        if (userId == null) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("error", "'user_id' field must not empty");
            responseBody.put("code", -1);

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
        }

        if (content == null) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("error", "'content' field must not empty");
            responseBody.put("code", -3);

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
        }

        // Check if member already exists.
        Optional<MemberEntity> result = memberRepository.findById(userId);

        if (result.isEmpty()) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("error", "User does not exist.");
            responseBody.put("code", -4);

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
        }

        // In case files does not exist.
        if (request.getFiles() == null) {
            MemberEntity member = result.get();

            PostEntity post = PostEntity.builder()
                    .content(content)
                    .writer(member)
                    .build();

            try {
                // Save post.
                PostEntity savedPostEntity = postRepository.save(post);

                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

                JSONObject data = new JSONObject();
                data.put("post_id", savedPostEntity.getId());
                data.put("content", content);

                JSONObject responseBody = new JSONObject();
                responseBody.put("timestamp", LocalDateTime.now());
                responseBody.put("status", HttpStatus.CREATED.value());
                responseBody.put("message", "Successfully posted.");
                responseBody.put("data", data);
                responseBody.put("code", 1);
                return new ResponseEntity(responseBody, responseHeaders, HttpStatus.CREATED);

            } catch (IllegalArgumentException e) {
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

                JSONObject responseBody = new JSONObject();
                responseBody.put("timestamp", LocalDateTime.now());
                responseBody.put("status", HttpStatus.BAD_REQUEST.value());
                responseBody.put("error", e.getLocalizedMessage());
                responseBody.put("code", -5);
                return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
            }
        }

        MultipartFile[] files = request.getFiles();

        List<String> imageUrls = new ArrayList<String>();

        for (MultipartFile file : files) {

            // Check if file is empty.
            if (file.isEmpty()) {

                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

                JSONObject responseBody = new JSONObject();
                responseBody.put("timestamp", LocalDateTime.now());
                responseBody.put("status", HttpStatus.BAD_REQUEST.value());
                responseBody.put("error", "File is empty.");
                responseBody.put("code", -6);

                return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
            }

            // Check if content-type is image
            if (!file.getContentType().startsWith("image")) {

                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

                JSONObject responseBody = new JSONObject();
                responseBody.put("timestamp", LocalDateTime.now());
                responseBody.put("status", HttpStatus.BAD_REQUEST.value());
                responseBody.put("error", "'Content-type' is invalid. 'Content-type' must be image.");
                responseBody.put("code", -7);

                return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
            }

            try {
                // Upload files
                String imageUrl = awsS3Uploader.upload(file);
                imageUrls.add(imageUrl);

            } catch (Exception e) {
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

                JSONObject responseBody = new JSONObject();
                responseBody.put("timestamp", LocalDateTime.now());
                responseBody.put("status", HttpStatus.BAD_REQUEST.value());
                responseBody.put("error", e.getLocalizedMessage());
                responseBody.put("code", -8);

                return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
            }
        }

        MemberEntity member = result.get();

        List<PostImageEntity> postImageEntities = new ArrayList<PostImageEntity>();

        for (String imageUrl : imageUrls) {
            PostImageEntity postImage = PostImageEntity.builder()
                    .imageUrl(imageUrl)
                    .build();

            postImageEntities.add(postImage);
        }

        PostEntity post = PostEntity.builder()
                .content(content)
                .writer(member)
                .imageUrls(postImageEntities)
                .build();

        try {
            // Save post.
            PostEntity savedPostEntity = postRepository.save(post);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject data = new JSONObject();
            data.put("post_id", savedPostEntity.getId());
            data.put("content", content);
            data.put("image_urls", imageUrls);

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.CREATED.value());
            responseBody.put("message", "Successfully posted.");
            responseBody.put("data", data);
            responseBody.put("code", 1);
            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("error", e.getLocalizedMessage());
            responseBody.put("code", -9);
            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<JSONObject> getPost(Long postId) {
        try {
            PostEntity postEntity = postRepository.getById(postId);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONArray imageUrls = new JSONArray();
            for (PostImageEntity imageUrl: postEntity.getImageUrls()) {
                imageUrls.add(imageUrl.getImageUrl());
            }

            JSONObject data = new JSONObject();
            data.put("post_id", postEntity.getId());
            data.put("content", postEntity.getContent());
            data.put("image_urls", imageUrls);

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("code", 1);
            responseBody.put("data", data);

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.OK);

        } catch (IllegalArgumentException e) {

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("error", e.getLocalizedMessage());
            responseBody.put("code", -1);

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);

        } catch (EntityNotFoundException e) {

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("error", e.getLocalizedMessage());
            responseBody.put("code", -2);

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);

        }
    }

    @Override
    public ResponseEntity<JSONObject> getPosts(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<PostEntity> result = postRepository.findAll(pageRequest);

        if (result.isEmpty()) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("error", "Posts do not exist.");
            responseBody.put("code", -1);

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
        }

        JSONObject jsonData = new JSONObject();

        JSONArray jsonPosts = new JSONArray();

        for (PostEntity postEntity: result) {
            JSONArray jsonImageUrls = new JSONArray();
            for (PostImageEntity postImageEntity: postEntity.getImageUrls()) {
                jsonImageUrls.add(postImageEntity.getImageUrl());
            }

            JSONObject jsonPost = new JSONObject();
            jsonPost.put("writer_id", postEntity.getWriter().getId());
            jsonPost.put("writer_email", postEntity.getWriter().getEmail());
            jsonPost.put("writer_nickname", postEntity.getWriter().getNickname());
            jsonPost.put("content", postEntity.getContent());
            jsonPost.put("image_urls", jsonImageUrls);

            jsonPosts.add(jsonPost);
        }

        jsonData.put("size", jsonPosts.size());
        jsonData.put("posts", jsonPosts);

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("code", "1");
        jsonBody.put("message", "Success");
        jsonBody.put("timestamp", LocalDateTime.now());
        jsonBody.put("data", jsonData);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        return new ResponseEntity<JSONObject>(jsonBody, responseHeaders, HttpStatus.OK);
    }
}
