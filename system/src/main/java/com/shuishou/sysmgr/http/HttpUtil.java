package com.shuishou.sysmgr.http;

import java.awt.Window;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.SerializableEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shuishou.sysmgr.ConstantValue;
import com.shuishou.sysmgr.beans.Category1;
import com.shuishou.sysmgr.beans.Category2;
import com.shuishou.sysmgr.beans.DiscountTemplate;
import com.shuishou.sysmgr.beans.Goods;
import com.shuishou.sysmgr.beans.GoodsSellRecord;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.beans.Member;
import com.shuishou.sysmgr.beans.MemberBalance;
import com.shuishou.sysmgr.beans.MemberScore;
import com.shuishou.sysmgr.beans.PayWay;
import com.shuishou.sysmgr.beans.Permission;
import com.shuishou.sysmgr.beans.Promotion;
import com.shuishou.sysmgr.beans.UserData;
import com.shuishou.sysmgr.ui.MainFrame;

public class HttpUtil {

	private final static Logger logger = Logger.getLogger("HttpUtil");
	
	public static HttpClient getHttpClient(){
		HttpParams mHttpParams=new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(mHttpParams, 20*1000);
        HttpConnectionParams.setSoTimeout(mHttpParams, 20*1000);
        HttpConnectionParams.setSocketBufferSize(mHttpParams, 8*1024);
        HttpClientParams.setRedirecting(mHttpParams, true);
          
        HttpClient httpClient=new DefaultHttpClient(mHttpParams);
        return httpClient;
	}
	
    public static String getJSONObjectByGet(String uriString){
//        JSONObject resultJsonObject=null;
    	String result = null;
        if ("".equals(uriString)||uriString==null) {
            return null;
        }
        HttpClient httpClient=getHttpClient();
        StringBuilder urlStringBuilder=new StringBuilder(uriString);
        StringBuilder entityStringBuilder=new StringBuilder();
        HttpGet httpGet=new HttpGet(urlStringBuilder.toString());
        BufferedReader bufferedReader=null;
        HttpResponse httpResponse=null;
        try {
            httpResponse=httpClient.execute(httpGet); 
        } catch (Exception e) {
        	logger.error("", e);
        }
        if (httpResponse == null)
        	return null;
        int statusCode=httpResponse.getStatusLine().getStatusCode();
        HttpEntity httpEntity=httpResponse.getEntity();
        if (httpEntity!=null) {
            try {
                bufferedReader=new BufferedReader(new InputStreamReader(httpEntity.getContent(), "UTF-8"), 8*1024);
                String line=null;
                while ((line=bufferedReader.readLine())!=null) {
                    entityStringBuilder.append(line+"\n");
                }
                if (statusCode==HttpStatus.SC_OK) {
                	return entityStringBuilder.toString();
//                	resultJsonObject=new JSONObject(entityStringBuilder.toString());
                } else {
                	logger.error("Http Error: URl : "+ uriString 
                			+ "\nhttpcode : "+ httpResponse.getStatusLine().getStatusCode()
                			+ "\nresponse message : " + entityStringBuilder.toString());
                }
                
            } catch (Exception e) {
            	logger.error("", e);
            }
        }
        
        return null;
    }
    
    public static String getJSONObjectByPost(String path,Map<String, String> params) {
    	return getJSONObjectByPost(path, params, "UTF-8");
    }
    public static String getJSONObjectByPost(String path,Map<String, String> paramsHashMap, String encoding) {
    	String result = null;
//        JSONObject resultJsonObject = null;
        List<NameValuePair> nameValuePairArrayList = new ArrayList<NameValuePair>();
        if (paramsHashMap != null && !paramsHashMap.isEmpty()) {
            for (Map.Entry<String, String> entry : paramsHashMap.entrySet()) {
                nameValuePairArrayList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
          
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(nameValuePairArrayList, encoding);
            HttpPost httpPost = new HttpPost(path);
            httpPost.setEntity(entity);
            HttpClient httpClient = getHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(  
                    new InputStreamReader(httpEntity.getContent(),"UTF-8"), 8 * 1024);
                    StringBuilder entityStringBuilder = new StringBuilder();
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        entityStringBuilder.append(line + "\n");
                    }
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
//                    	resultJsonObject = new JSONObject(entityStringBuilder.toString());
                    	return entityStringBuilder.toString();
                    } else {
                    	logger.error("Http Error: URl : "+ path + "\nparam : "+ paramsHashMap 
                    			+ "\nhttpcode : "+ httpResponse.getStatusLine().getStatusCode()
                    			+ "\nresponse message : " + entityStringBuilder.toString());
                    }
                    
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
            
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    } 
    
    public static String getJSONObjectByPostSerialize(String path,SerializableEntity entity, String encoding) {
    	String result = null;
          
        try {
            HttpPost httpPost = new HttpPost(path);
            httpPost.setEntity(entity);
            HttpClient httpClient = getHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(  
                    new InputStreamReader(httpEntity.getContent(),"UTF-8"), 8 * 1024);
                    StringBuilder entityStringBuilder = new StringBuilder();
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        entityStringBuilder.append(line + "\n");
                    }
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
//                    	resultJsonObject = new JSONObject(entityStringBuilder.toString());
                    	return entityStringBuilder.toString();
                    } else {
                    	logger.error("Http Error: URl : "+ path + "\nentity : "+ entity 
                    			+ "\nhttpcode : "+ httpResponse.getStatusLine().getStatusCode()
                    			+ "\nresponse message : " + entityStringBuilder.toString());
                    }
                    
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
            
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    } 
    
    public static String getJSONObjectByUploadFile(String url, HashMap<String, ContentBody> params){
    	String result = null;
    	try {
        	HttpClient httpclient = getHttpClient();
            HttpPost httppost = new HttpPost(url);

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            for(String s : params.keySet()){
            	entityBuilder.addPart(s, params.get(s));
            }
            HttpEntity reqEntity = entityBuilder.build();
            httppost.setEntity(reqEntity);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null){
            	try {
                    BufferedReader bufferedReader = new BufferedReader(  
                    new InputStreamReader(httpEntity.getContent(),"UTF-8"), 8 * 1024);
                    StringBuilder entityStringBuilder = new StringBuilder();
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        entityStringBuilder.append(line + "\n");
                    }
                    if (response.getStatusLine().getStatusCode() == 200) {
//                    	resultJsonObject = new JSONObject(entityStringBuilder.toString());
                    	return entityStringBuilder.toString();
                    } else {
                    	logger.error("Http Error: URl : "+ url + "\nparams : "+ params 
                    			+ "\nhttpcode : "+ response.getStatusLine().getStatusCode()
                    			+ "\nresponse message : " + entityStringBuilder.toString());
                    }
                    
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }
    
    /**
	 * this class just hold Category1 objects. if need dish object, please loop into the category1 objects
	 */
	public static ArrayList<Category1> loadGoods(JFrame parent, String url){
		String response = getJSONObjectByGet(url);
		if (response == null){
			logger.error("get null from server for loading goods. URL = " + url);
			JOptionPane.showMessageDialog(parent, "get null from server for loading goods. URL = " + url);
			return null;
		}
		HttpResult<ArrayList<Category1>> result = new Gson().fromJson(response, new TypeToken<HttpResult<ArrayList<Category1>>>(){}.getType());
		if (!result.success){
			logger.error("return false while loading goods. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		//repoint category2 and dishes to their parent
		ArrayList<Category1> c1s = result.data;
		for(Category1 c1 : c1s){
			for(Category2 c2 : c1.getCategory2s()){
				c2.setCategory1(c1);
				for(Goods goods : c2.getGoods()){
					goods.setCategory2(c2);
				}
			}
		}
		return result.data;
	}
	
	public static ArrayList<UserData> loadUser(JFrame parent, String url){
		String response = getJSONObjectByGet(url);
		if (response == null){
			logger.error("get null from server for loading user. URL = " + url);
			JOptionPane.showMessageDialog(parent, "get null from server for loading user. URL = " + url);
			return null;
		}
		HttpResult<ArrayList<UserData>> result = new Gson().fromJson(response, new TypeToken<HttpResult<ArrayList<UserData>>>(){}.getType());
		if (!result.success){
			logger.error("return false while loading user. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
	
	public static ArrayList<Permission> loadPermission(JFrame parent, String url){
		String response = getJSONObjectByGet(url);
		if (response == null){
			logger.error("get null from server for loading Permission. URL = " + url);
			JOptionPane.showMessageDialog(parent, "get null from server for loading Permission. URL = " + url);
			return null;
		}
		HttpResult<ArrayList<Permission>> result = new Gson().fromJson(response, new TypeToken<HttpResult<ArrayList<Permission>>>(){}.getType());
		if (!result.success){
			logger.error("return false while loading Permission. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
	
	public static ArrayList<PayWay> loadPayWay(JFrame parent, String url){
		String response = getJSONObjectByGet(url);
		if (response == null){
			logger.error("get null from server for loading pay way. URL = " + url);
			JOptionPane.showMessageDialog(parent, "get null from server for loading pay way. URL = " + url);
			return null;
		}
		HttpResult<ArrayList<PayWay>> result = new Gson().fromJson(response, new TypeToken<HttpResult<ArrayList<PayWay>>>(){}.getType());
		if (!result.success){
			logger.error("return false while loading pay way. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
	
	public static ArrayList<Promotion> loadPromotion(JFrame parent, String url){
		String response = getJSONObjectByGet(url);
		if (response == null){
			logger.error("get null from server for loading promotion. URL = " + url);
			JOptionPane.showMessageDialog(parent, "get null from server for loading promotion. URL = " + url);
			return null;
		}
		HttpResult<ArrayList<Promotion>> result = new Gson().fromJson(response, new TypeToken<HttpResult<ArrayList<Promotion>>>(){}.getType());
		if (!result.success){
			logger.error("return false while loading promotion. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
	
	public static ArrayList<DiscountTemplate> loadDiscountTemplate(JFrame parent, String url){
		String response = getJSONObjectByGet(url);
		if (response == null){
			logger.error("get null from server for loading Discount Template. URL = " + url);
			JOptionPane.showMessageDialog(parent, "get null from server for loading Discount Template. URL = " + url);
			return null;
		}
		HttpResult<ArrayList<DiscountTemplate>> result = new Gson().fromJson(response, new TypeToken<HttpResult<ArrayList<DiscountTemplate>>>(){}.getType());
		if (!result.success){
			logger.error("return false while loading Discount Template. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
	
	public static ArrayList<String> loadLogType(JFrame parent, String url){
		String response = getJSONObjectByGet(url);
		if (response == null){
			logger.error("get null from server for loading log type. URL = " + url);
			JOptionPane.showMessageDialog(parent, "get null from server for loading log type. URL = " + url);
			return null;
		}
		HttpResult<ArrayList<String>> result = new Gson().fromJson(response, new TypeToken<HttpResult<ArrayList<String>>>(){}.getType());
		if (!result.success){
			logger.error("return false while loading log type. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
	
	public static HashMap<String, String> loadConfigMap(JFrame parent, String url){
		String response = getJSONObjectByGet(url);
		if (response == null){
			logger.error("get null from server for loading configs. URL = " + url);
			JOptionPane.showMessageDialog(parent, "get null from server for loading configs. URL = " + url);
			return null;
		}
		HttpResult<HashMap<String, String>> result = new Gson().fromJson(response, new TypeToken<HttpResult<HashMap<String, String>>>(){}.getType());
		if (!result.success){
			logger.error("return false while loading configs. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
	
	public static ArrayList<GoodsSellRecord> loadGoodsSellRecord(JFrame parent, String url, Map<String, String> params){
		String response = getJSONObjectByPost(url, params, "UTF-8");
		if (response == null){
			logger.error("get null from server for loading goods sold record. URL = " + url + ", params = "+ params);
			JOptionPane.showMessageDialog(parent, "get null from server for loading goods sold record. URL = " + url+ ", params = "+ params);
			return null;
		}
		HttpResult<ArrayList<GoodsSellRecord>> result = new Gson().fromJson(response, new TypeToken<HttpResult<ArrayList<GoodsSellRecord>>>(){}.getType());
		if (!result.success){
			logger.error("return false while loading goods sold record. URL = " + url + ", params = "+ params+", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
	
	public static Member loadMember(Window parent, UserData user, String memberCard){
		String url = "member/querymember";
		Map<String, String> params = new HashMap<>();
		params.put("userId", user.getId() + "");
		params.put("memberCard", memberCard);
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		if (response == null){
			logger.error("get null from server for query member by membercard. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(parent, "get null from server for query member by membercard. URL = " + url);
			return null;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<ArrayList<Member>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<Member>>>(){}.getType());
		if (!result.success){
			logger.error("return false while query member by membercard. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data.get(0);
	}
	
	public static ArrayList<Member> loadAllMember(Window parent, UserData user){
		String url = "member/queryallmember";
		Map<String, String> params = new HashMap<>();
		
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		if (response == null){
			logger.error("get null from server for query all member. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(parent, "get null from server for query all member. URL = " + url);
			return null;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<ArrayList<Member>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<Member>>>(){}.getType());
		if (!result.success){
			logger.error("return false while query all member. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
	
	public static ArrayList<MemberBalance> loadMemberBalanceRecord(Window parent, int userId, int memberId){
		String url = "member/querymemberbalance";
		Map<String, String> params = new HashMap<>();
		params.put("userId", String.valueOf(userId));
		params.put("memberId", String.valueOf(memberId));
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		if (response == null){
			logger.error("get null from server for query member balance. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(parent, "get null from server for query member balance. URL = " + url);
			return null;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<ArrayList<MemberBalance>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<MemberBalance>>>(){}.getType());
		if (!result.success){
			logger.error("return false while query member balance. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
	
	public static ArrayList<MemberScore> loadMemberScoreRecord(Window parent, int userId, int memberId){
		String url = "member/querymemberscore";
		Map<String, String> params = new HashMap<>();
		params.put("userId", String.valueOf(userId));
		params.put("memberId", String.valueOf(memberId));
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		if (response == null){
			logger.error("get null from server for query member score. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(parent, "get null from server for query member score. URL = " + url);
			return null;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<ArrayList<MemberScore>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<MemberScore>>>(){}.getType());
		if (!result.success){
			logger.error("return false while query member score. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
}
