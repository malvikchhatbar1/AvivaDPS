package com.aviva.dps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.aviva.dps.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

public class DpsProcessor extends Activity {

	static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
	private static String callToService = new String();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set the main content layout of the Activity
		setContentView(R.layout.activity_main1);
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		this.setTitle(getResources().getString(R.string.app_header));
	}

	//Scan QR Code:
	public void scanQR(View v) {
		try {
			//start the scanning activity from the com.google.zxing.client.android.SCAN intent
			Intent intent = new Intent(ACTION_SCAN);
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			startActivityForResult(intent, 0);
		} catch (ActivityNotFoundException anfe) {
			//on catch, show the download dialog
			showDialog(DpsProcessor.this, "No QR Code Scanner Found", "Download a scanner code?", "Yes", "No").show();
		}
	}

	//Alert dialog for downloading qr code scanner app
	private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
		AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
		downloadDialog.setTitle(title);
		downloadDialog.setMessage(message);
		downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int i) {
				Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				try {
					act.startActivity(intent);
				} catch (ActivityNotFoundException anfe) {

				}
			}
		});
		downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int i) {
			}
		});
		return downloadDialog.show();
	}

	//on ActivityResult method
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				//get the extras that are returned from the intent
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				String content = "Content:" + contents;
				
				setContentView(R.layout.activity_main1);
				

				TextView contentTVKey =  (TextView) findViewById(R.id.textView2);
				TextView contentTV =  (TextView) findViewById(R.id.textView3);
				contentTV.setText(content);
				contentTVKey.setVisibility(View.INVISIBLE);
				contentTV.setVisibility(View.INVISIBLE);
				
				String x = contents.substring(0, contents.indexOf("lookup"));
				String[] y = contents.split("/lookup.jsp?p");
				
				y[1] = ((y[1].replace("&", "/")).replace("=", "/")).replace("?", "api/");
				String newApiCall=x+y[1];
				newApiCall=newApiCall.substring(0, newApiCall.indexOf("/company"));
				System.out.println(newApiCall);
				
				callToService = newApiCall;
				Button btn2 = (Button) findViewById(R.id.getPolicy);
				btn2.setEnabled(true);
			}
		}
	}
	
	//Get Policy
	public void getPolicy(View v) throws Exception {
		
		//Make httpUrlConnection Call:		
		//Retrieve from GSON:

		System.out.println("A");

        DpsLookupPojo gsonOutputStringFromJson = getPolicyDetailsResponse(callToService);
		
		//Set Values: (Main)
		/*
        TextView policyNumberValView =  (TextView) findViewById(R.id.policyNumberVal);
        policyNumberValView.setText(gsonOutputStringFromJson.getPolicyNumber());
        TextView effectiveDateValView =  (TextView) findViewById(R.id.effectiveDateVal);
        effectiveDateValView.setText(gsonOutputStringFromJson.getEffectiveDate());
        TextView expiryDateValView =  (TextView) findViewById(R.id.expiryDateVal);
        expiryDateValView.setText(gsonOutputStringFromJson.getExpirationDate());
        TextView nameOfInsuredValView =  (TextView) findViewById(R.id.nameOfInsuredVal);
        nameOfInsuredValView.setText(gsonOutputStringFromJson.getPolicyholderName());
        TextView addressOfInsuredValView =  (TextView) findViewById(R.id.addressOfInsuredVal);
        addressOfInsuredValView.setText(gsonOutputStringFromJson.getPolicyholderAddress());
        TextView yearView =  (TextView) findViewById(R.id.year);
        yearView.setText(gsonOutputStringFromJson.getYear());
        TextView makeView =  (TextView) findViewById(R.id.make);
        makeView.setText(gsonOutputStringFromJson.getMake());
        TextView modelView =  (TextView) findViewById(R.id.model);
        modelView.setText(gsonOutputStringFromJson.getModel());
        TextView vinView =  (TextView) findViewById(R.id.vin);
        vinView.setText(gsonOutputStringFromJson.getVin());
        TextView liabilityCardTypeView =  (TextView) findViewById(R.id.liabilityCardType);
        liabilityCardTypeView.setText("Motor Vehicle Liability Insurance Card\n"+gsonOutputStringFromJson.getLiabilityCardType());
        
		*/
		
		//Set Values: (Main1)
        String string = gsonOutputStringFromJson.getExpiryDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        java.util.Date dateExpiry = format.parse(string);
        //System.out.println(dateExpiry);
        
        String dateNowA = new SimpleDateFormat("EEEE dd MMMM, yyyy").format(Calendar.getInstance().getTime()); 
        String dateNowB = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()); 

        //System.out.println(dateNowA);
        //System.out.println(dateNowB);
        
        setContentView(R.layout.activity_main1);
        TextView a = (TextView) findViewById(R.id.textViewDate);
        a.setText(dateNowA);
        
        java.util.Date dateNow = format.parse(dateNowB);
        
        TextView policyStatusValView =  (TextView) findViewById(R.id.policyStatusVal);
	       
        if(dateNow.before(dateExpiry))
        {
        	//Green
        	System.out.println("Valid - Green");
            policyStatusValView.setText("VALID");	
            policyStatusValView.setTextColor(Color.GREEN);
        }
        else if(dateNow.equals(dateExpiry))
        {
        	//Yellow - Expires Today
        	System.out.println("Expires Today - Yellow");
            policyStatusValView.setText("EXPIRES TODAY!");	
            policyStatusValView.setTextColor(Color.YELLOW);
        }
        else if(dateNow.after(dateExpiry))
        {
        	//Red
        	System.out.println("Invalid - Red");
            policyStatusValView.setText("INVALID");	
            policyStatusValView.setTextColor(Color.RED);
        }
        
        TextView insuranceProviderValView =  (TextView) findViewById(R.id.insuranceProviderVal);
        insuranceProviderValView.setText(gsonOutputStringFromJson.getInsuranceCompany());		
        TextView policyNumberValView =  (TextView) findViewById(R.id.policyNumberVal);
        policyNumberValView.setText(gsonOutputStringFromJson.getPolicyNumber());
        TextView expiryDateValView =  (TextView) findViewById(R.id.expiryDateVal);
        expiryDateValView.setText(gsonOutputStringFromJson.getExpiryDate());
        TextView nameOfInsuredValView =  (TextView) findViewById(R.id.nameOfInsuredVal);
        nameOfInsuredValView.setText(gsonOutputStringFromJson.getPolicyholderName());
        TextView makeView =  (TextView) findViewById(R.id.makeVal);
        makeView.setText(gsonOutputStringFromJson.getVehicleMake());
        TextView modelView =  (TextView) findViewById(R.id.modelVal);
        modelView.setText(gsonOutputStringFromJson.getVehicleModel());
        TextView vinView =  (TextView) findViewById(R.id.vinVal);
        vinView.setText(gsonOutputStringFromJson.getVin());
        
		//Set Visibility (Main1):
		
		findViewById(R.id.scanner).setVisibility(View.INVISIBLE);
		findViewById(R.id.textView2).setVisibility(View.INVISIBLE);
		findViewById(R.id.textView3).setVisibility(View.INVISIBLE);
		findViewById(R.id.getPolicy).setVisibility(View.INVISIBLE);
		
		findViewById(R.id.textViewDate).setVisibility(View.VISIBLE);
		findViewById(R.id.insuranceProviderKey).setVisibility(View.VISIBLE);
		findViewById(R.id.insuranceProviderVal).setVisibility(View.VISIBLE);
		findViewById(R.id.policyNumberKey).setVisibility(View.VISIBLE);
		findViewById(R.id.policyNumberVal).setVisibility(View.VISIBLE);
		findViewById(R.id.expiryDateKey).setVisibility(View.VISIBLE);
		findViewById(R.id.expiryDateVal).setVisibility(View.VISIBLE);
		findViewById(R.id.policyStatusKey).setVisibility(View.VISIBLE);
		findViewById(R.id.policyStatusVal).setVisibility(View.VISIBLE);
		findViewById(R.id.nameAddOfInsuredKey).setVisibility(View.VISIBLE);
		findViewById(R.id.nameOfInsuredVal).setVisibility(View.VISIBLE);
		findViewById(R.id.makeKey).setVisibility(View.VISIBLE);
		findViewById(R.id.makeVal).setVisibility(View.VISIBLE);
		findViewById(R.id.modelKey).setVisibility(View.VISIBLE);
		findViewById(R.id.modelVal).setVisibility(View.VISIBLE);
		findViewById(R.id.vinKey).setVisibility(View.VISIBLE);
		findViewById(R.id.vinVal).setVisibility(View.VISIBLE);
				
	}
	
    public static DpsLookupPojo getPolicyDetailsResponse(String serviceUrlOutput) throws  Exception{

        Gson gson = new GsonBuilder().create();                             
        DpsLookupPojo DpsLookupPojoOutput = gson.fromJson(getPolicyLookupResponseinStringFormat(serviceUrlOutput), DpsLookupPojo.class) ;
        return DpsLookupPojoOutput;
    }
    
    public static String  getPolicyLookupResponseinStringFormat(String getPolicyLookupServiceURLStr)
    //throws DAException
	{
    	String policyServiceOutput = null;
    	StringBuilder stringBuilder = new StringBuilder();
		
		try
		{
			URL serviceUrl = new URL(getPolicyLookupServiceURLStr);
            HttpURLConnection httpURLConnection = (HttpURLConnection) serviceUrl.openConnection();
            
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            
            
            //Actual Call:
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((httpURLConnection.getInputStream())));
            while ((policyServiceOutput = bufferedReader.readLine()) != null) {
                            stringBuilder.append(policyServiceOutput);
            }
            httpURLConnection.disconnect();
		}
		catch (MalformedURLException e) {
			System.out.println("MalformedURLException: " + e);
		}
		catch (ProtocolException e) {
			System.out.println("ProtocolException: " + e);
		}
		catch (IOException e) {
			System.out.println("IOException: " + e);
		}
		
		String restCallOutput = stringBuilder.toString();
		System.out.println("Output from REST Call Before");
		System.out.println(restCallOutput);
		restCallOutput=restCallOutput.substring(1,(restCallOutput.length()-1));
		System.out.println("Output from REST Call After");
		System.out.println(restCallOutput);
		
		return restCallOutput;
	}



}