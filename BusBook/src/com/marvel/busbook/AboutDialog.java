package com.marvel.busbook;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AboutDialog extends Dialog {
//public class AboutDialog extends Activity {

	public AboutDialog(Context context) {
		super(context);

	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        
        Button btnOK = (Button)findViewById(R.id.btnAbout);
        btnOK.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AboutDialog.this.dismiss();
				
			}
		});
	}

}
