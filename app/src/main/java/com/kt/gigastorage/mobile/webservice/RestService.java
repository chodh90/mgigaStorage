package com.kt.gigastorage.mobile.webservice;

import com.google.gson.JsonObject;
import com.kt.gigastorage.mobile.vo.ComndQueueVO;
import com.kt.gigastorage.mobile.vo.DevBasVO;
import com.kt.gigastorage.mobile.vo.FileBasVO;
import com.kt.gigastorage.mobile.vo.FileEmailVO;
import com.kt.gigastorage.mobile.vo.FileTagVO;
import com.kt.gigastorage.mobile.vo.FoldrBasVO;
import com.kt.gigastorage.mobile.vo.NoteBasVO;
import com.kt.gigastorage.mobile.vo.NoteBmarkVO;
import com.kt.gigastorage.mobile.vo.NoteListVO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by a-raise on 2016-09-08.
 */
public interface RestService {

    //로그인
    @FormUrlEncoded
    @POST("login.do")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Call<JsonObject> login(@Field("userId") String userId, @Field("password") String password);

    //로그아웃
    @POST("logout.do")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Call<JsonObject> logout();

    //디바이스인증
    @POST("devAthn.do")
    Call<JsonObject> devBasAuth(@Body DevBasVO devBasVO);

    //Oneview리스트
    @POST("listOneview.json")
    Call<JsonObject> listOneview(@Body DevBasVO devBasVO);

    //폴더리스트
    @POST ("listFoldr.json" )
    Call<JsonObject> listFoldr(@Body FoldrBasVO foldrBasVO);

    //파일리스트
    @POST ("listFile.json" )
    Call<JsonObject> listFile(@Body FileBasVO fileBasVO);

    //싱크 폴더 List조회
    @POST ("mobileFoldrList.json" )
    Call<JsonObject> syncFoldrInfo(@Body FoldrBasVO foldrBasVO);

    //싱크 파일 List조회
    @POST ("mobileFileList.json" )
    Call<JsonObject> syncFileInfo(@Body FileBasVO fileBasVO);

    //파일메타정보등록
    @POST ("mobileFileMetaInfoList.do" )
    Call<JsonObject> fileMetaInsert(@Body List<FileBasVO> fileBasVO);

    //폴더메타정보등록
    @POST ("mobileFoldrMetaInfoList.do" )
    Call<JsonObject> foldrMetaInsert(@Body List<FoldrBasVO> list);

    //파일메타정보삭제
    @POST ("mobileFileMetaInfoList.do" )
    Call<JsonObject> deleteFileMeta(@Body List<FileBasVO> list);

    //폴더메타정보삭제
    @POST ("mobileFoldrMetaInfoList.do" )
    Call<JsonObject> deleteFoldrMeta(@Body List<FoldrBasVO> list);

    @POST("reqFileDown.do")
    Call<JsonObject> nasFileDownload(@Body ComndQueueVO comndQueueVO);

    //파일 속성 조회
    @POST ("fileDtl.json" )
    Call<JsonObject> fileAttrList(@Body FileBasVO fileBasVO);

    //파일,폴더 검색
    @POST("listSearch.json")
    Call<JsonObject> listSearchFoldrFile(@Body FoldrBasVO foldrBasVO);

    //파일,폴더 검색
    @POST("listFileSharSearch.json")
    Call<JsonObject> FileSharList(@Body FoldrBasVO foldrBasVO);

    @POST("updDevNm.do")
    Call<JsonObject> updDevNm(@Body DevBasVO devBasVO);

    @POST("nasUpldCmplt.do")
    Call<JsonObject> nasUpldCmplt(@Body ComndQueueVO comndQueueVO);

    @POST("nasFileCopy.do")
    Call<JsonObject> nasFileCopy(@Body FileBasVO fileBasVO);

    @POST("nasFileDel.do")
    Call<JsonObject> nasFileDel(@Body FileBasVO fileBasVO);

    @POST("mobileFileTagUpdate.do")
    Call<JsonObject> mobileFileTagUpdate(@Body FileTagVO fileTagVO);

    @POST("fileSharUpdate.do")
    Call<JsonObject> fileSharUpdate(@Body FileBasVO fileBasVO);

    @POST("listNoteMenu.json")
    Call<JsonObject> listNoteMenu(@Body NoteBasVO noteBasVO);

    @POST("mergNoteBmark.do")
    Call<JsonObject> mergNoteBmark(@Body NoteBmarkVO noteBmarkVO);

    @POST("delNoteBmark.do")
    Call<JsonObject> delNoteBmark(@Body NoteBmarkVO noteBmarkVO);

    @POST("listNoteBmark.json")
    Call<JsonObject> listNoteBmark(@Body NoteBmarkVO noteBmarkVO);

    @POST("listNote.json")
    Call<JsonObject> listNote(@Body NoteListVO noteListVO);

    @POST("listNoteAsc.json")
    Call<JsonObject> listNoteAsc(@Body NoteListVO noteListVO);

    @POST("listFileSame.json")
    Call<JsonObject> listFileSame(@Body FileBasVO fileBasVO);

    @POST("listSendEmailSame.json")
    Call<JsonObject> listSendEmailSame(@Body FileEmailVO fileEmailVO);

    @POST("listRefrEmail.json")
    Call<JsonObject> listRefrEmail(@Body FileEmailVO fileEmailVO);
}
