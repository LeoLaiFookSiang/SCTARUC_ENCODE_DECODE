package leo.com.mqtt;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import java.math.BigInteger;

public class MainActivity extends AppCompatActivity {

    EditText editTextName,editTextYear,editTextPassword;
    RadioButton radMale,radFemale;
    Spinner spinner;
    TextView txtViewEncodeResult;
    static int decodeCurrentCursor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextName = (EditText) findViewById(R.id.editTextName);
        radMale = (RadioButton) findViewById(R.id.radMale);
        radFemale = (RadioButton) findViewById(R.id.radFemale);
        spinner = (Spinner) findViewById(R.id.spinnerProgramme);
        editTextYear = (EditText) findViewById(R.id.editTextYear);
        editTextPassword = (EditText) findViewById(R.id.editTextPass);
        txtViewEncodeResult = (TextView) findViewById(R.id.txtViewEncodeResult);

    }

    public void btnDecode(View v) {//525344
        String subscribeData = "124c6565205761682050656e67000002525344000003066c656f313233";
        if(subscribeData.length()%2 == 0){
            char nameLengthFirstDigit = subscribeData.charAt(0);
            char nameLengthSecondDigit = subscribeData.charAt(1);
            decodeCurrentCursor += 1;
            int nameSize = Integer.parseInt(String.valueOf(nameLengthFirstDigit)+String.valueOf(nameLengthSecondDigit));
            editTextName.setText(hexToAscii(splitString(nameSize,subscribeData)));

            if(subscribeData.charAt(decodeCurrentCursor + 6) == '1'){
                radMale.setChecked(true);
            }else if(subscribeData.charAt(decodeCurrentCursor + 6) == '2'){
                radFemale.setChecked(true);
            }else{

            }
            decodeCurrentCursor += 6;

            String programmeCode = hexToAscii(splitString(3,subscribeData));
            if(programmeCode.equals("RSD")){
                spinner.setSelection(0);
            }else if(programmeCode.equals("RIT")){
                spinner.setSelection(1);
            }

            editTextYear.setText(subscribeData.charAt(decodeCurrentCursor + 6)+"");
            decodeCurrentCursor += 6;

            char passwordLengthFirstDigit = subscribeData.charAt(decodeCurrentCursor+1);
            char passwordLengthSecondDigit = subscribeData.charAt(decodeCurrentCursor+2);
            decodeCurrentCursor += 2;
            int passwordSize = Integer.parseInt(String.valueOf(passwordLengthFirstDigit)+String.valueOf(passwordLengthSecondDigit));
            editTextPassword.setText(hexToAscii(splitString(passwordSize,subscribeData)));
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Data is not completed")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void btnEncode(View v) {
        String encodeText = "";
        String name = editTextName.getText().toString();
        String password = editTextPassword.getText().toString();
        String gender = "";
        if(radMale.isChecked()){
            gender = "000001";
        }else if(radFemale.isChecked()){
            gender = "000002";
        }
        String programmeCode = spinner.getSelectedItem().toString();
        String numOfYear = "00000" + editTextYear.getText().toString();

        encodeText += name.length() + asciiToHex(name) + gender + asciiToHex(programmeCode) + numOfYear
                + password.length() + asciiToHex(password);

        txtViewEncodeResult.setText("Encode Result: \n" + encodeText);

        byte[] b = new BigInteger(encodeText,16).toByteArray();

        for(int i=0; i < b.length; i++){
            Log.d("Byte" + i, b[i] + "");
        }


    }

    public String splitString(int length, String subscribeData){
        String completeString = "";
        for(int i = 0 ; i < length*2 ; i++){
            decodeCurrentCursor ++;
            completeString += subscribeData.charAt(decodeCurrentCursor);
        }
        return completeString;
    }

    public String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    public String asciiToHex(String asciiStr) {
        char[] chars = asciiStr.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char ch : chars) {
            hex.append(Integer.toHexString((int) ch));
        }

        return hex.toString();
    }

}
