package com.crypsis.wordgamedemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
     
	String word_url="http://api.wordnik.com:80/v4/words.json/randomWord?hasDictionaryDef=false&minCorpusCount=0&maxCorpusCount=-1&minDictionaryCount=1&maxDictionaryCount=-1&minLength=5&maxLength=-1&api_key=a2a73e7b926c924fad7001ca3111acd55af2ffabf50eb4ae5";
    String def_url=null;
    String word_rel_url=null;
	EditText word_text;
	EditText def_text;
	String real_word;
	Button chk;
	Button next_word;
	Button tryAgain;
	Button hint;
	Button endGame;
	int point=20;
	static int high_score=0;
	int hint_val=0;
	JSONArray jsonDef = null;
	
	//create Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //UI elements references
        word_text = (EditText) findViewById(R.id.word);
        def_text = (EditText) findViewById(R.id.def);
        chk=(Button) findViewById(R.id.check);
        next_word = (Button) findViewById(R.id.next);
        tryAgain = (Button) findViewById(R.id.tryAgain);
        hint = (Button) findViewById(R.id.hint);
        endGame = (Button) findViewById(R.id.end);
        
        tryAgain.setEnabled(false);
        hint.setEnabled(false);
        //onclicklistener
        
        endGame.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),"score="+point, Toast.LENGTH_LONG).show();
				if(point>high_score)
				{
					high_score=point;
					Toast.makeText(getApplicationContext(), "Yaye U score highest!!!", Toast.LENGTH_SHORT).show();
				finish();
			   }
			}
		});
        chk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				String val=word_text.getText().toString();
				if(val.equals(real_word))
				{
					Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_LONG).show();
					point+=10;
					word_text.setText("");
					jsonDef =null;
					hint_val=0;
					new HttpAsyncTask_word().execute(word_url);	 
					
				}
				else
				{
					Toast.makeText(getApplicationContext(), "wrong", Toast.LENGTH_LONG).show();
					if(hint_val>0)
						point-=1;
					else
						point-=2;
					word_text.setText("");
				
					tryAgain.setEnabled(true);
					hint.setEnabled(true);
				
				}
			}
		}); 
        
        tryAgain.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				word_text.setEnabled(true);
				
			}
		});
        
        hint.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 hint_val+=1;
				 JSONObject jObj=null;
				  try {
					jObj = jsonDef.getJSONObject(hint_val);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
					if((jsonDef.length())==hint_val)
					{
						hint_val--;
						point-=hint_val;
						hint.setEnabled(false);
						chk.setEnabled(false);
						tryAgain.setEnabled(false);
						Toast.makeText(getApplicationContext(), "word is "+real_word+" try next word ", Toast.LENGTH_LONG).show();
					}
					else{
					try {
						Toast.makeText(getApplicationContext(),jObj.getString("text"),Toast.LENGTH_LONG).show();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				       }
				
		
				
		
       
        }
        });
        next_word.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				hint_val=0;
				word_text.setText("");
				word_text.setEnabled(true);
				hint.setEnabled(true);
				chk.setEnabled(true);
				tryAgain.setEnabled(true);
				jsonDef=null;
				Toast.makeText(getApplicationContext(),"word is :"+real_word, Toast.LENGTH_LONG).show();
				new HttpAsyncTask_word().execute(word_url);
			}
		});
        //checking internet connection
        if(isConnected())
        {
        	 Toast toast=Toast.makeText(getApplicationContext(),"connected", Toast.LENGTH_LONG);
        	 toast.show();
        }
         else
         {
        	 Toast toast=Toast.makeText(getApplicationContext(),"Not connected", Toast.LENGTH_LONG);
        	 toast.show();
         }
        new HttpAsyncTask_word().execute(word_url);	 
        
    }
    
    //to get result for corresponding url
    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
 
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
 
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
 
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
 
            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        return result;
    }
    
    //convert result to String
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
 
    }
 
    
    //function to check Internet connection
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) 
                return true;
            else
                return false;   
    }
    
    
    //async task to call get function and post the result [word]
    private class HttpAsyncTask_word extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
 
            return GET(urls[0]);
        }
		// onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
			JSONObject json = null;
			try {
				json = new JSONObject(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				real_word=json.getString("word");
				definition(real_word);
				Toast.makeText(getApplicationContext(), real_word, Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				
					e.printStackTrace();
			}
			
       }

        //to get the definition of word
		private void definition(String word) {
			// TODO Auto-generated method stub
			def_url = "http://api.wordnik.com:80/v4/word.json/"+word+"/definitions?limit=200&includeRelated=true&useCanonical=false&includeTags=false&api_key=a2a73e7b926c924fad7001ca3111acd55af2ffabf50eb4ae5";
			new HttpAsyncTask_def().execute(def_url);
			word_rel_url="http://api.wordnik.com:80/v4/word.json/"+word+"/relatedWords?useCanonical=false&limitPerRelationshipType=10&api_key=a2a73e7b926c924fad7001ca3111acd55af2ffabf50eb4ae5";
		}
    }
        
     //async function to check the definition of random word and post the result
        private class HttpAsyncTask_def extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... urls) {
     
                return GET(urls[0]);
            }
            
    		// onPostExecute displays the results of the AsyncTask.
            @Override
            protected void onPostExecute(String result) {
    			
    			JSONObject jObj =null;
    			try {
    				jsonDef = new JSONArray(result);
    				jObj = jsonDef.getJSONObject(0);
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			try {
					def_text.setText(jObj.getString("text"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
           }
        }
        
       /*//to find synonym of word
        private class HttpAsyncTask_synonym extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... urls) {
     
                return GET(urls[0]);
            }
            
    		// onPostExecute displays the results of the AsyncTask.
            @Override
            protected void onPostExecute(String result) {
    			JSONObject jArr = null;
    			JSONArray jarrSynonymArr =null;
    			try {
    				jArr = new JSONObject(result);
    				jarrSynonymArr
    				
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		
    			
           }
        }
        
       */
    }
    
