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
import org.json.simple.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.yologger.heart_to_heart_springboot.util.AwsS3Uploader;

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
        String title = request.getTitle();
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

        if (title == null) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("error", "'title' field must not empty");
            responseBody.put("code", -2);

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

        MultipartFile[] files = request.getFiles();

        List<String> imageUrls = new ArrayList<String>();

        // Upload files
        for (MultipartFile file : files) {

            // Check if invalid content-type
            if (!file.getContentType().startsWith("image")) {

                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

                JSONObject responseBody = new JSONObject();
                responseBody.put("timestamp", LocalDateTime.now());
                responseBody.put("status", HttpStatus.BAD_REQUEST.value());
                responseBody.put("error", "'Content-type' is invalid. 'Content-type' must be image.");
                responseBody.put("code", -4);

                return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
            }

            try {

                 String imageUrl = awsS3Uploader.upload(file);
                 imageUrls.add(imageUrl);

            } catch (Exception e) {
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

        MemberEntity member = result.get();

        List<PostImageEntity> postImageEntities = new ArrayList<PostImageEntity>();

        for (String imageUrl: imageUrls) {
            PostImageEntity postImage = PostImageEntity.builder()
                    .imageUrl(imageUrl)
                    .build();

            postImageEntities.add(postImage);
        }

        PostEntity post = PostEntity.builder()
                .title(title)
                .content(content)
                .writer(member)
                .imageUrls(postImageEntities)
                .build();

        try {
            // Save post.
            postRepository.save(post);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject data = new JSONObject();
            data.put("title", title);
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
            responseBody.put("code", -6);
            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }
}
