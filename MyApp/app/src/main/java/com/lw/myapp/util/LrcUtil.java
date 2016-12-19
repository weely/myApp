package com.lw.myapp.util;

import android.os.Environment;
import android.util.Log;

import com.lw.myapp.model.LrcInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lw on 2016/12/16.
 */

public class LrcUtil {

    public static List<LrcInfo> readLRC(String path) {
        List<LrcInfo> lrcLists = new ArrayList<LrcInfo>();
        try {
            if (path.contains(".mp3")) {
                File f = new File(path.replace(".mp3", ".lrc"));
                FileInputStream fis = new FileInputStream(f);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                String line = "";
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("[ti:")) {
                        lrcLists.add(new LrcInfo(line.substring(4, line.lastIndexOf("]")), 0));
                    } else if (line.startsWith("[ar:")) {
                        //  lrcLists.add(new LrcInfo(s.substring(4, s.lastIndexOf("]")),2500));
                    } else if (line.startsWith("[al:")) {
                        //	lrcLists.add(new LrcInfo(s.substring(4, s.lastIndexOf("]")),5000));
                    } else if (line.startsWith("[by:")) {
                        //	lrcLists.add(new LrcInfo(s.substring(4, s.lastIndexOf("]")),7500));
                    } else if (line.startsWith("[la:")) {
                    } else {
                        line = line.replace("[", "");
                        String splitLrcData[] = line.split("]");
                        int len = splitLrcData.length;
                        for (int i = 0; i < len; i++) {
                            LrcInfo mLrcInfo = new LrcInfo();
                            if (i < len - 1) {
                                int lrcTime = formatStrToTime(splitLrcData[i]);
                                mLrcInfo.setLrcContent(splitLrcData[len - 1] + "");
                                mLrcInfo.setLrcTime(lrcTime);
                                lrcLists.add(mLrcInfo);
                            }
                        }
                    }
                }
                br.close();
                isr.close();
                fis.close();
            }
        } catch (FileNotFoundException e) {
            Log.e("weely", "-文件未发现-");
            return null;
        } catch (IOException e) {
            Log.e("weely", "-文件读取异常-");
            return null;
        }
        Log.i("weely", "--歌曲读取成功--");
        return lrcLists;
    }

    public static int formatStrToTime(String timeStr) {
        String timeDatas[] = timeStr.replace(".", ":").split(":");

        int minute = Integer.parseInt(timeDatas[0]);
        int second = Integer.parseInt(timeDatas[1]);
        int millisecond = timeDatas.length == 3 ? Integer.parseInt(timeDatas[2]) : 0;

        return (minute * 60 + second) * 1000 + millisecond * 10;
    }

    public static InputStream getLrcFromNet(String singer, String songName) {
        String PATH = "http://geci.me/api/lyric/";
        InputStream is = null;
        BufferedReader reader = null;
        StringBuffer sb = null;
        InputStream isLrc = null;
        try {
            PATH += songName + "/" + singer;
            URL url = new URL(PATH);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));
            sb = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            conn.disconnect();
            reader.close();
            is.close();

            isLrc = jsonParser(sb.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return is;
    }

    public static InputStream jsonParser(String url) {
        InputStream is = null;
        try {
            JSONObject jo = new JSONObject(url);
            JSONArray jsonArray = jo.getJSONArray("result");
            JSONObject Object = jsonArray.getJSONObject(0);
            String lrcPath = Object.getString("lrc");
            URL lrcUrl = new URL(lrcPath);
            HttpURLConnection connection = (HttpURLConnection) lrcUrl.openConnection();
            connection.connect();
            is = connection.getInputStream();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return is;
    }

    public static void downLrc(String fileUrl, InputStream is) {
        fileUrl = fileUrl.replace(".mp3", ".lrc");
        FileOutputStream os = null;
        BufferedReader reader = null;
        StringBuffer sb = null;
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.i("weely","sd卡不存在");
            return;
        }
        try {
            File file = new File(fileUrl);
			/*if(file.exists()){
				Log.i("weely", "歌词已存在");
				return;
			}*/
            os = new FileOutputStream(file);
            reader = new BufferedReader(new InputStreamReader(is));
            sb = new StringBuffer();
            String line = null;
            while((line = reader.readLine())!=null){
                sb.append(line+"\r\n");
                Log.i("weely", line);
            }
            os.write(sb.toString().getBytes());
            os.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
