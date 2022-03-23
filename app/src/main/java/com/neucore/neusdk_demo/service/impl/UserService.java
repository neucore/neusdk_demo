package com.neucore.neusdk_demo.service.impl;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.neucore.neusdk_demo.service.db.UserDaoUtils;
import com.neucore.neusdk_demo.service.db.bean.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

final public class UserService extends AbsUserService {
    private String TAG = "UserService";
    private HashMap<String, User> user_hm = new HashMap<>();
    private ArrayList<byte[]> feature_org = new ArrayList<byte[]>();
    private ArrayList<byte[]> feature_mask = new ArrayList<byte[]>();
    private ArrayList<String> name_org = new ArrayList<String>();
    private static UserService instance = new UserService();
    private Object lock = new Object();
    private Gson gson=new Gson();
    private static Context context;
    private static UserDaoUtils mUserDaoUtils;

    public static UserService getInstance(Context _context){
        if(mUserDaoUtils==null)
            mUserDaoUtils = new UserDaoUtils(_context);
            synchronized (UserService.class){
                if(mUserDaoUtils==null){
                    mUserDaoUtils = new UserDaoUtils(_context);
                    context = _context;
                }
            }
        return instance;
    }

    public void load(){

        List<User> users = mUserDaoUtils.queryAllUser();
        int size = users==null?0:users.size();
        List<User> tmpUsers = new ArrayList<User>();
        for(int i=0;i<size;i++){
            User user = users.get(i);
            String faceStr = user.getFace();
            if (!TextUtils.isEmpty(faceStr)) {
                tmpUsers.add(user);
            }
            else {
                mUserDaoUtils.deleteUserByUserId(user.getUserId());
                Log.i(TAG,"user:"+user.getUserId() +" face is null");
            }
        }
        HashMap<String, User> user_hm = new HashMap<>();
        ArrayList<byte[]> feature_org = new ArrayList<byte[]>();
        ArrayList<byte[]> feature_mask = new ArrayList<byte[]>();
        ArrayList<String> name_org = new ArrayList<String>();

        size = tmpUsers==null?0:tmpUsers.size();
        for (int i = 0; i < size; i++) {

            User user = tmpUsers.get(i);
            String faceStr=user.getFace();
            try {
                if (!TextUtils.isEmpty(faceStr)) {
                    user_hm.put(user.getUserId(), user);
                    //2.字符串转成Byte[]
                    Byte[] b = gson.fromJson(faceStr, Byte[].class);
                    //3.将值赋值给byte[]数组，此步骤可能多余
                    byte[] c = new byte[b.length];
                    for (int j = 0; j < b.length; j++) {
                        c[j] = b[j];
                        //System.out.println(c[j]);
                    }
                    feature_org.add(c);
                }
            }catch (Exception e){
                Log.e(TAG,"CardId: "+user.getCardId(),e );
            }


            String facemaskStr = user.getFaceMask();
            try {
                if (!TextUtils.isEmpty(facemaskStr)) {
                    //2.字符串转成Byte[]
                    Byte[] b = gson.fromJson(facemaskStr, Byte[].class);
                    //3.将值赋值给byte[]数组，此步骤可能多余
                    byte[] c = new byte[b.length];
                    for (int j = 0; j < b.length; j++) {
                        c[j] = b[j];
                        //System.out.println(c[j]);
                    }
                    feature_mask.add(c);
                    name_org.add(user.getUserId());
                }
            }catch (Exception e){
                Log.e(TAG,"CardId: "+user.getCardId(),e );
            }
        }
        synchronized (lock){
            this.user_hm = user_hm;
            this.feature_org = feature_org;
            this.feature_mask = feature_mask;
            this.name_org = name_org;
        }
    }

    /**
     * 通过员工工号获取当前用户对象
     * @param userid 员工工号
     * @return 用户对象
     */
    public User getUser(String userid){
        return user_hm.get(userid);
    }

    public synchronized HashMap<String, User> getUsers(){
        return user_hm;
    }

    public synchronized ArrayList<byte[]> getFeatures(){
        return feature_org;
    }

    public synchronized ArrayList<byte[]> getMaskFeatures(){
        return feature_mask;
    }

    public synchronized ArrayList<String> getNames(){
        return name_org;
    }
}
