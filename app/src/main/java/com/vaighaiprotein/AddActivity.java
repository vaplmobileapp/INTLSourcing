package com.vaighaiprotein;



import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.InputFilter.LengthFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddActivity extends Activity implements OnClickListener  {
private Button btn_save;
private ListView listView;
private EditText area,edit_last,doc_date,inputsearch,freighval,Othval,Loading;
private Spinner branch;
private DbHelper mHelper;
private SQLiteDatabase dataBase;
private String id,fname,lname,freight,othchg,branchnam,Loaddate,Loadchg;
private boolean isUpdate;
static final int DATE_DIALOG_ID = 100;
private ArrayList<String> ARE_ANAME = new ArrayList<String>();
private ArrayAdapter<String> adapter;
DbHelper database;
private String best;
ConnectionDetector cd;
Boolean isInternetPresent = false;
String s1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.add_activity);
             
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);        
        doc_date=(EditText) findViewById(R.id.Dat);
        btn_save=(Button)findViewById(R.id.save_btn);
        area=(EditText)findViewById(R.id.frst_editTxt);
        edit_last=(EditText)findViewById(R.id.last_editTxt); 
        freighval=(EditText) findViewById(R.id.editText1);       
        Othval=(EditText) findViewById(R.id.oth_chg);
        Loading=(EditText) findViewById(R.id.Loadingchg);     
        branch=(Spinner) findViewById(R.id.spinner1);      
        isUpdate=getIntent().getExtras().getBoolean("update");
       
        cd = new ConnectionDetector(getApplicationContext());
        
        if(isUpdate)
        {
        	id=getIntent().getExtras().getString("ID");
        	fname=getIntent().getExtras().getString("Fname");
        	lname=getIntent().getExtras().getString("Lname");      	
        	area.setText(fname);
        	edit_last.setText(lname);
        	
        	
        }
         
         btn_save.setOnClickListener(this);
         
         mHelper=new DbHelper(this);
        
         doc_date.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 showDialog(DATE_DIALOG_ID);
				
				
			}
		});
         
         
         area.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				searchCitiesList();				
				
			}
		});
     
    }    
  
	@Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
        case DATE_DIALOG_ID:
           // set date picker as current date
        	   final Calendar c = Calendar.getInstance();
              int year = c.get(Calendar.YEAR);
              int month = c.get(Calendar.MONTH);
              int day = c.get(Calendar.DAY_OF_MONTH);
        	
       return new DatePickerDialog(this, datePickerListener, year, month,day);
        }
        return null;
    }
        
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
   
    	
        public void onDateSet(DatePicker view, int selectedYear,int selectedMonth, int selectedDay) {
        	selectedMonth=selectedMonth+1;
        	 String formattedMonth = "" + selectedMonth;
    	        String formattedDayOfMonth = "" + selectedDay;
    	        if(selectedMonth < 10){

    	        	formattedMonth = ("0" + formattedMonth);
    	        }
    	        if(selectedDay < 10){

    	        	formattedDayOfMonth = "0" + formattedDayOfMonth;
    	        }
    	        String docdate=((formattedDayOfMonth+"/"+formattedMonth+"/"+selectedYear));    	    
        	s1=formattedDayOfMonth+"/"+formattedMonth+"/"+selectedYear;
        	doc_date.setText(docdate);
    		
    		
        }
    };    
    
	@SuppressWarnings("null")
	private void setCurrentDate() {		
		  DatePicker date_picker = null;
	        final Calendar calendar = Calendar.getInstance();
	 
	      int   year = calendar.get(Calendar.YEAR);
	       int month = calendar.get(Calendar.MONTH);
	       int day = calendar.get(Calendar.DAY_OF_MONTH);
	 
	        // set current date into textview
	       doc_date.setText(new StringBuilder()
	            // Month is 0 based, so you have to add 1
	            .append(day).append("/")
	            .append(month + 1).append("/")
	            .append(year).append(" "));
	 
	        // set current date into Date Picker
	       date_picker.init(year, month, day, null);
		
	} 
   
    // saveButton click event 
	public void onClick(View v) {
		fname=area.getText().toString().trim();
		lname=edit_last.getText().toString().trim();
		freight= freighval.getText().toString().trim(); 
		othchg=Othval.getText().toString().trim();
		branchnam=branch.getSelectedItem().toString();
		Loaddate=doc_date.getText().toString().trim();		
		Loadchg=Loading.getText().toString().trim();		
		isInternetPresent = cd.isConnectingToInternet();
		
		if(fname.length()>0 && lname.length()>0 && branchnam.length()>0 && Loaddate.length()>0 && isInternetPresent )
		{
			saveData();
		}
		else
		{
			AlertDialog.Builder alertBuilder=new AlertDialog.Builder(AddActivity.this);
			alertBuilder.setTitle("Invalid Data");
			alertBuilder.setMessage("Please, Enter valid data");
			alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
					
				}
			});
			alertBuilder.create().show();
		}
		
	}

	private void saveData(){
		
		
		
		 long rowcnt = 	(mHelper.load_getrowid());
		
		dataBase=mHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		
		values.put(DbHelper.KEY_FNAME,fname);
		values.put(DbHelper.KEY_LNAME,lname );
		values.put(DbHelper.KEY_Freight,freight );
		values.put(DbHelper.KEY_Other,othchg );
		values.put(DbHelper.KEY_Branch,branchnam );
		values.put(DbHelper.KEY_docdate,Loaddate );
		values.put(DbHelper.Table_rownum,rowcnt );
		values.put(DbHelper.KEY_Load,Loadchg );
		
		//System.out.println("");
		if(isUpdate)
		{    
			
			dataBase.update(DbHelper.TABLE_NAME, values, DbHelper.Table_rownum+"="+id, null);
		}
		else
		{
			//insert data into database
			dataBase.insert(DbHelper.TABLE_NAME, null, values);
			 rowcnt = 	(mHelper.load_getrowid());
			 InputStream webs = null;
			
			 lname=lname.replace(" ", "%20");
			 fname=fname.replace(" ", "%20");
			 
			 TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			 String imeino=telephonyManager.getDeviceId();		
			 
				try
				{
					HttpClient httpclient = new DefaultHttpClient();
					String surl = "http://223.30.82.99:8080/protein/loadbasic.php?Loaddate="+Loaddate+"&fname="+fname+"&lname="+lname+"&branchnam="+branchnam+"&freight="+freight+"&othchg="+othchg+"&MRV="+rowcnt+"&imeino="+imeino+"&load="+Loadchg;
					HttpPost httppost = new HttpPost(surl);			
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					webs = entity.getContent();	
					
				}
				catch (Exception e)
				{		 	  
			   
				}
		}		
		dataBase.close();
		finish();		
		
	}
	
	public void searchCitiesList() {
		ARE_ANAME.clear();
        final Dialog dialog = new Dialog(AddActivity.this);
        dialog.setContentView(R.layout.area);
        dialog.setTitle("Select Area");
        listView = (ListView) dialog.findViewById(R.id.list);
        inputsearch =  (EditText) dialog.findViewById(R.id.editText1);      
        database=new DbHelper(AddActivity.this);    
      
    		dataBase = mHelper.getWritableDatabase();
    		Cursor mCursor = dataBase.rawQuery("SELECT * FROM "
    				+ DbHelper.Table_Area , null);
    		
    		if (mCursor.moveToFirst()) {
    			do {
    				ARE_ANAME.add(mCursor.getString(mCursor.getColumnIndex(DbHelper.Ar_Name)));
   				
   			} while (mCursor.moveToNext());
    		}   		
    		
    	
        dialog.show();
        
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ARE_ANAME); 
     
        listView.setAdapter(adapter);        
        
        listView.setTextFilterEnabled(true);
        
        
       listView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                int itemPosition = position;
                String itemValue = (String) listView
                        .getItemAtPosition(position);
                area.setText(itemValue);
                dialog.cancel();

            }

        });
        
       inputsearch.addTextChangedListener(new TextWatcher() {
    	   
           @Override
           public void onTextChanged(CharSequence s, int start, int before,
                   int count) {
               adapter.getFilter().filter(s.toString());
           }

           @Override
           public void beforeTextChanged(CharSequence s, int start, int count,
                   int after) {
           }

           @Override
           public void afterTextChanged(Editable s) {
           }
       });
   }
	
}
