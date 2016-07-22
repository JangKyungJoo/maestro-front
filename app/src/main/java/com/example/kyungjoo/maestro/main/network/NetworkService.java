package com.example.kyungjoo.maestro.main.network;

import com.example.kyungjoo.maestro.main.Response;
import com.example.kyungjoo.maestro.main.anonymityboard.detail.AnonymityContent;
import com.example.kyungjoo.maestro.main.anonymityboard.thumbnail.AnonyThumbnail;
import com.example.kyungjoo.maestro.main.board.detail.Content;
import com.example.kyungjoo.maestro.main.board.thumbnail.Thumbnail;
import com.example.kyungjoo.maestro.main.people.UserProfile;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by KyungJoo on 2016-07-13.
 */
public interface NetworkService {

    @GET("anonymity")
    Call<List<AnonyThumbnail>> getAnonyThumbnail();

    @GET("anonymity/{article-id}")
    Call<AnonymityContent> getAnonyContent(@Path("article-id") long id);

    @POST("anonymity/{article-id}")
    Call<AnonyThumbnail> newAnony(@Path("article-id") String id,
                                  @Body AnonymityContent content);

    @HTTP(method = "DELETE", path = "anonymity/{userid}", hasBody = false)
    Call<List<AnonyThumbnail>> deleteAnony(@Path("userid") String uid,
                                           @Query("articleid") String id);

    @Multipart
    @POST("people")
    Call<Response> newPeople(@Part("id") RequestBody id,
                             @Part("name") RequestBody name,
                             @Part("profile") RequestBody profile);

    @GET("people/{article-id}")
    Call<Response> checkPeople(@Path("article-id") String id);

    @GET("people/profile/{article-id}")
    Call<UserProfile> getUserProfile(@Path("article-id") String id);

    @Multipart
    @POST("board/{article-id}")
    Call<Thumbnail> newContent(@Path("article-id") String id,
                               @Part("title")RequestBody title,
                               @Part("content")RequestBody content,
                               @Part MultipartBody.Part photo);

    @GET("board")
    Call<List<Thumbnail>> getThumbnail();

    @GET("board/{article-id}")
    Call<Content> getContent(@Path("article-id") long id);

    @HTTP(method = "DELETE", path = "board/{userid}", hasBody = false)
    Call<List<Thumbnail>> deleteContent(@Path("userid") String uid,
                                        @Query("articleid") String id);

}
