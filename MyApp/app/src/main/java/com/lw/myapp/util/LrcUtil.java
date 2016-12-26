package com.lw.myapp.util;

import android.os.Environment;
import android.util.Log;

import com.lw.myapp.model.LrcInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.universalchardet.UniversalDetector;

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
import java.util.Collections;
import java.util.List;

/**
 * Created by Lw on 2016/12/16.
 */

public class LrcUtil {

    public static List<LrcInfo> readLRC(String path) {
        List<LrcInfo> lrcLists = new ArrayList<LrcInfo>();
        File f = null;
        FileInputStream fis = null;
        BufferedReader reader = null;
        InputStreamReader isr = null;
        try {
            if (path.contains(".mp3")) {
                f = new File(path.replace(".mp3", ".lrc"));
                fis = new FileInputStream(f);
                String encoding = getFileIncode(f);
                if (encoding != null) {
                    if ("UTF-8".equals(encoding.toUpperCase())) {
                        isr = new InputStreamReader(fis, "UTF-8");
                    } else if ("GB".equals(encoding.substring(0, 2).toUpperCase())) {
                        isr = new InputStreamReader(fis, "GBK");
                    } else {
                        isr = new InputStreamReader(fis, "UTF-8");
                    }
                } else {
                    isr = new InputStreamReader(fis, "UTF-8");
                }
                reader = new BufferedReader(isr);
                String line = "";
                int lrcTime = 0;
                while ((line = reader.readLine()) != null) {
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
                        if (line.startsWith("[x-trans]")) {
                            lrcLists.add(new LrcInfo(line.substring(line.lastIndexOf("]") + 1), lrcTime));
                        } else {
                            String splitLrcData[] = line.replace("[", "").split("]");
                            int len = splitLrcData.length;
                            for (int i = 0; i < len; i++) {
                                LrcInfo mLrcInfo = new LrcInfo();
                                if (i < len - 1) {
                                    lrcTime = formatStrToTime(splitLrcData[i]);
                                    mLrcInfo.setLrcContent(splitLrcData[len - 1] + "");
                                    mLrcInfo.setLrcTime(lrcTime);
                                    lrcLists.add(mLrcInfo);
                                }
                            }
                        }
                    }
                }
                reader.close();
                isr.close();
                fis.close();
            }
        } catch (FileNotFoundException e) {
            Log.e("weely", "-文件未发现-");
            return null;
        } catch (IOException e) {
            Log.e("weely", "-文件读取异常-");
            return null;
        } catch (Exception e) {
            Log.e("weely", "-文件读取异常-");
            return null;
        }
        Log.i("weely", "--歌曲读取成功--");
        Collections.sort(lrcLists);

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
        String PATH = "http://gecimi.com/api/lyric/";
        InputStream is = null;
        BufferedReader reader = null;
        StringBuffer sb = null;
        try {
            PATH += songName + "/" + singer;
            URL url = new URL(PATH);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(2000);
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            conn.connect();
            is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            sb = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            conn.disconnect();
            reader.close();
            is.close();
            return jsonParser(sb.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream jsonParser(String url) {
        InputStream is = null;
        try {
            JSONObject jo = new JSONObject(url);
            JSONArray jsonArray = jo.getJSONArray("result");
            JSONObject Object = jsonArray.getJSONObject(0);
            String lrcPath = Object.getString("lrc");
            if ("".equals(lrcPath)) {
                return null;
            }
            URL lrcUrl = new URL(lrcPath);
            HttpURLConnection connection = (HttpURLConnection) lrcUrl.openConnection();
            connection.setConnectTimeout(1000);
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            connection.connect();
            is = connection.getInputStream();
            return is;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void downLrc(String fileUrl, InputStream is) {
        fileUrl = fileUrl.replace(".mp3", ".lrc");
        FileOutputStream os = null;
        BufferedReader reader = null;
        StringBuffer sb = null;
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.i("weely", "sd卡不存在");
            return;
        }
        try {
            File file = new File(fileUrl);
            if(file.exists()){
                Log.i("weely", "歌词已存在");
				return;
			}
            os = new FileOutputStream(file);
            reader = new BufferedReader(new InputStreamReader(is));
            sb = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\r\n");
            }
            os.write(sb.toString().getBytes());
            os.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件编码
     *
     * @param file 文件
     * @return 返回编码格式
     */
    public static String getFileIncode(File file) {
        if (!file.exists()) {
            System.err.println("getFileIncode: file not exists!");
            return null;
        }
        byte[] buf = new byte[4096];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            UniversalDetector detector = new UniversalDetector(null);
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();
            String encoding = detector.getDetectedCharset();
            detector.reset();
            fis.close();
            return encoding;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
