package com.sustcse.besafe.alertbanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


public class AlertDialogForAnything {

	public AlertDialogForAnything(){
		
	}
	
	public static void showAlertDialogWhenComplte(Context context, String title, String message, Boolean status) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		 
		alertDialog.setTitle(title);

		alertDialog.setMessage(message);
		
		alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
            	dialog.cancel();
            
            }
        });
 
        alertDialog.show();
	}
	
	
}
