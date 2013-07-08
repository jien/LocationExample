package com.example.locationexample;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends FragmentActivity implements
				 GooglePlayServicesClient.ConnectionCallbacks,
				 GooglePlayServicesClient.OnConnectionFailedListener,
				 LocationListener{

	private Button mButton;
	private TextView mTextView;
	private LocationClient mLocaitonClient;
	private LocationRequest mLocationRequest;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (Button)findViewById(R.id.obtain_address_btn);
        mTextView = (TextView)findViewById(R.id.address_txt);
        mLocaitonClient = new LocationClient(this,this,this);
        mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isServiceConnected()){
					getLocation();
				}
			}
		});
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        
    }

    

    @Override
	protected void onStart() {
		super.onStart();
		mLocaitonClient.connect();
    }



	@Override
	protected void onStop() {
		mLocaitonClient.removeLocationUpdates(this);
		mLocaitonClient.disconnect();
		super.onStop();
	}



	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    
    @Override
	public void onLocationChanged(Location location) {
    	new GeocoderLocationTask(this).execute(new Location[]{location});
	}


	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onConnected(Bundle connectionHint) {
		mLocaitonClient.requestLocationUpdates(mLocationRequest, this);
	}


	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}


	private boolean isServiceConnected(){
    	int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    	if(resultCode == ConnectionResult.SUCCESS){
    		return true;
    	}else{
    		showErrorDialog(resultCode);
    	}
    	return false;
    }
	
	private void showErrorDialog(int errorCode){
		Dialog dialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this, 1);
		ErrorDialogFrament errorDialogFragment = new ErrorDialogFrament();
		errorDialogFragment.setDialog(dialog);
		errorDialogFragment.show(getSupportFragmentManager(), "ErrorDialogFragment");
	}
    
    private void getLocation(){
    	Location location = mLocaitonClient.getLastLocation();
    	new GeocoderLocationTask(this).execute(new Location[]{location});
    }
    
    public static class ErrorDialogFrament extends DialogFragment{

    	Dialog mDialog;
    	
		public ErrorDialogFrament() {
			super();
		}

		public void setDialog(Dialog dialog){
			mDialog = dialog;
		}
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
		
    }
    public  class GeocoderLocationTask extends AsyncTask<Location,Void,String>{

    	private Context mContext;
    	
		public GeocoderLocationTask(Context context) {
			super();
			mContext = context;
		}

		@Override
		protected String doInBackground(Location... params) {
			Location loc = params[0];
			Geocoder geocoder = new Geocoder(mContext,Locale.getDefault());
			String addressText="";
			try {
				List<Address> addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
				if(addresses != null && addresses.size()>0){
					Address address = addresses.get(0);
					StringBuilder sb = new StringBuilder();
					for(int i=0;i<address.getMaxAddressLineIndex();i++){
						sb.append(address.getAddressLine(i));
					}
					addressText = sb.toString();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return addressText;
		}

		@Override
		protected void onPostExecute(String result) {
			
//			Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
			mTextView.setText(result);
		}
    	
    }
    
}
