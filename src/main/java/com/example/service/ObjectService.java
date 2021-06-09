package com.example.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObjectService {
	private final S3Client s3Client;

	public void putObject(String key) {

		CreateBucketRequest bucketRequest = CreateBucketRequest.builder().bucket("foo").build();
		try {
			s3Client.createBucket(bucketRequest);
		} catch (BucketAlreadyExistsException e) {
			log.warn("bucket {} already exists", "foo");
		}

		PutObjectRequest putOb = PutObjectRequest.builder().bucket("foo").key(key).build();
		s3Client.putObject(putOb, RequestBody.fromBytes("baz".getBytes()));
	}

}
