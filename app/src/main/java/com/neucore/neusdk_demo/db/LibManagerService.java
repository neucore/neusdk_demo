package com.neucore.neusdk_demo.db;

import android.content.Context;
import android.util.Log;

import com.neucore.neusdk_demo.db.bean.LicNumber;
import com.neucore.neusdk_demo.db.bean.User;
import com.neucore.neulink.rrpc.FaceData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LibManagerService {

    private String TAG = "LibManagerService";
    private UserDaoUtils mUserDaoUtils;
    private LicNumDaoUtils licNumDaoUtils;

    public LibManagerService(Context context){
        mUserDaoUtils = new UserDaoUtils(context);
        licNumDaoUtils = new LicNumDaoUtils(context);
    }

    /**
     * 根据ext_id删除数据
     * @param params
     */
    public void deleteFacelib(List<FaceData> params){
        int size = params==null?0:params.size();
        for(int i=0;i<size;i++){
            FaceData param= params.get(i);
            mUserDaoUtils.deleteUserByUserId(String.valueOf(param.getExtId()));
        }
    }

    public void deleteLiclib(List<String> params){
        int size = params==null?0:params.size();
        for(int i=0;i<size;i++){
            String param= params.get(i);
            licNumDaoUtils.deleteLicByLicNum(param);
        }
    }

    public void deleteFacelibByReqTime(long reqTime){
        mUserDaoUtils.delete(reqTime);
    }

    public void deleteLicNumberLibByReqTime(long reqTime){
        licNumDaoUtils.delete(reqTime);
    }

    public boolean insertOrUpdLicNumlib(long reqTime,List<String> params){
        int size = params==null?0:params.size();
        for(int i=0;i<size;i++){

            String param= params.get(i);

            List<LicNumber> tmp = licNumDaoUtils.queryLicNumber(param,0);
            if(tmp!=null&&tmp.size()>0){

                licNumDaoUtils.updateLicNum(reqTime,param);
                Log.i(TAG,"updated LicNumber: "+param);
            }
            else{
                LicNumber licNumber = new LicNumber();
                licNumber.setLicNum(param);
                licNumber.setTime(reqTime);
                licNumDaoUtils.insertLicNumber(licNumber);
                Log.i(TAG,"inserted LicNumber: "+param);
            }
        }

        return true;
    }

    public boolean insertOrUpdFacelib(long reqTime,List<FaceData> params){
        int size = params==null?0:params.size();
        for(int i=0;i<size;i++){

            FaceData param= params.get(i);

            List<User> tmp = mUserDaoUtils.queryCardId(String.valueOf(param.getExtId()),0);
            if(tmp!=null&&tmp.size()>0){
                User user = new User();
                user.setUserId(String.valueOf(param.getExtId()));
                user.setCardId(String.valueOf(param.getExtId()));
                user.setName(param.getName());
                user.setOrg(param.getOrg());
                user.setFace(param.getFace());
                user.setFaceMask(param.getFaceMask());
                user.setTime(reqTime);
                mUserDaoUtils.updateUser(String.valueOf(param.getExtId()),user);
                Log.i(TAG,"updated card_id: "+user.getCardId());
            }
            else{
                User user = new User();
                user.setUserId(String.valueOf(param.getExtId()));
                user.setCardId(String.valueOf(param.getExtId()));
                user.setName(param.getName());
                user.setOrg(param.getOrg());
                user.setFace(param.getFace());
                user.setFaceMask(param.getFaceMask());
                user.setTime(reqTime);
                mUserDaoUtils.insertUser(user);
                Log.i(TAG,"inserted: "+user.get_id()+";card_id: "+user.getCardId());
            }
        }

        return true;
    }
    /**
     *
     * 根据ext_id更新数据
     * @param params
     * @param images
     * @return
     */
    public boolean insertOrUpdFacelib(long reqTime, List<FaceData> params, Map images){

        int size = params==null?0:params.size();
        List<User> users = new ArrayList<User>();
        for(int i=0;i<size;i++){

            FaceData param= params.get(i);
            Map image = (Map)images.get(String.valueOf(param.getExtId()));
            if(image==null){//表示没有找到有效的特征图片数据
                continue;
            }
            List<User> tmp = mUserDaoUtils.queryCardId(String.valueOf(param.getExtId()),0);
            if(tmp!=null&&tmp.size()>0){
                User user = new User();
                user.setUserId(String.valueOf(param.getExtId()));
                user.setCardId(String.valueOf(param.getExtId()));
                user.setName(param.getName());
                user.setOrg(param.getOrg());
                user.setPhotoType((String)image.get("image_type"));
                if(image.get("face_sid")==null || image.get("face_sid_mask") == null ){
                    continue;
                }
                user.setFace((String) image.get("face_sid"));
                user.setFaceMask((String) image.get("face_sid_mask"));
                user.setTime(reqTime);
                mUserDaoUtils.updateUser(String.valueOf(param.getExtId()),user);
                Log.i(TAG,"updated card_id: "+user.getCardId());
            }
            else{
                User user = new User();
                user.setUserId(String.valueOf(param.getExtId()));
                user.setCardId(String.valueOf(param.getExtId()));
                user.setName(param.getName());
                user.setOrg(param.getOrg());
                user.setPhotoType((String)image.get("image_type"));
                if(image.get("face_sid")==null || image.get("face_sid_mask") == null ){
                    continue;
                }
                user.setFace((String) image.get("face_sid"));
                user.setFaceMask((String) image.get("face_sid_mask"));
                user.setTime(reqTime);
                mUserDaoUtils.insertUser(user);
                Log.i(TAG,"inserted: "+user.get_id()+";card_id: "+user.getCardId());
            }
        }

        return true;
    }

    public boolean insertOrUpdFacelib(long reqTime,User[] params){
        int len = params==null?0:params.length;
        for(int i=0;i<len;i++){
            User param = params[i];
            List<User> tmp = mUserDaoUtils.queryCardId(String.valueOf(param.getUserId()),0);
            if(tmp!=null&&tmp.size()>0){
                User user = new User();
                user.setUserId(String.valueOf(param.getUserId()));
                user.setCardId(String.valueOf(param.getUserId()));
                user.setName(param.getName());
                user.setOrg(param.getOrg());
                user.setFace(param.getFace());
                user.setFaceMask(param.getFaceMask());
                user.setTime(reqTime);
                mUserDaoUtils.updateUser(String.valueOf(param.getUserId()),user);
            }
            else{
                User user = new User();
                user.setUserId(String.valueOf(param.getUserId()));
                user.setCardId(String.valueOf(param.getUserId()));
                user.setName(param.getName());
                user.setOrg(param.getOrg());
                user.setFace(param.getFace());
                user.setFaceMask(param.getFaceMask());
                user.setTime(reqTime);
                mUserDaoUtils.insertUser(user);
            }
        }
        return true;
    }
    /**
     * 批量插入下发数据
     * @param params
     * @param images
     */
    public void sync(long reqTime, List<FaceData> params, Map images){

        int size = params==null?0:params.size();
        for (int i=0;i<size;i++){
            FaceData param= params.get(i);
            String userId = param.getExtId();
            Map image = (Map)images.get(String.valueOf(userId));

            User user = new User();
            user.setUserId(String.valueOf(param.getExtId()));
            user.setCardId(String.valueOf(param.getExtId()));
            user.setName(param.getName());
            user.setPhotoType((String)image.get("image_type"));
            user.setFace((String) image.get("face_sid"));
            user.setTime(reqTime);
            mUserDaoUtils.insertUser(user);
        }
    }

    public List<User> queryAllUser(){
        return mUserDaoUtils.queryAllUser();
    }
}
