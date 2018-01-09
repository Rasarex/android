package com.example.newpc.qrcode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.decoder.Version;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.QRCode;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Hashtable;
import java.util.Random;

public class GeneratorActivity extends AppCompatActivity {
    EditText text;
    Button gen_btn;
    Button button;
    ImageView image;
    String text2Qr;

    final String[] charTable=new String[]{"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u"
    ,"w","x","y","z","0","1","2","3","4","5","6","7","8","9"};
    private ByteMatrix ChangeToByteMatrix(BitMatrix bitmatrix){
        ByteMatrix byteMatrix=new ByteMatrix(bitmatrix.getWidth(),bitmatrix.getHeight());
        for(int i=0;i<bitmatrix.getWidth();i++){
            for(int k=0;k<bitmatrix.getHeight();k++){
                byteMatrix.set(i,k,bitmatrix.get(i,k));
            }
        }
        return byteMatrix;

    }
    private BitMatrix ChangeToBitMatrix(ByteMatrix byteMatrix){
        BitMatrix bitMatrix=new BitMatrix(byteMatrix.getWidth(),byteMatrix.getHeight());
        bitMatrix.clear();
        for(int i=0;i<byteMatrix.getWidth();i++){
            for(int k=0;k<byteMatrix.getHeight();k++){
                int a=byteMatrix.get(i,k);
                if(a!=0){
                    bitMatrix.set(i,k);
                }
            }
        }
        return bitMatrix;
    }
    private BitMatrix ChangeMask(BitMatrix matrix){
        QRCode qrCode=new QRCode();
        BitMatrix bit=new BitMatrix(1,2);

        try{
            qrCode.setMatrix(ChangeToByteMatrix(matrix));
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error setting Matrix",Toast.LENGTH_SHORT).show();
        }
        try {
            do {
                qrCode.setMaskPattern(0);
            }while(qrCode.getMaskPattern()!=0);

            qrCode.setVersion(Version.getVersionForNumber( 1 ));
            qrCode.setECLevel(ErrorCorrectionLevel.L);
            //qrCode.setMode(Mode.BYTE);
        }catch(Exception e) {
            Toast.makeText(getApplicationContext(),"Error setting Mask pattern",Toast.LENGTH_SHORT).show();
        }
        try {
            return ChangeToBitMatrix(qrCode.getMatrix());
        }catch(Exception e) {
            Toast.makeText(getApplicationContext(),"Error changing to bit matrix ",Toast.LENGTH_SHORT).show();
        }
        return bit;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);
        text = (EditText) findViewById(R.id.text);
        gen_btn = (Button) findViewById(R.id.gen_btn);
        image = (ImageView) findViewById(R.id.image);
        gen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rand = new Random();
                int n=rand.nextInt(5);

                text2Qr ="";
                for(int i=0;i<=n+5;i++) {
                    int k = rand.nextInt(charTable.length);
                    text2Qr += charTable[k];
                }
                Toast.makeText(getApplicationContext(),text2Qr,Toast.LENGTH_SHORT).show();
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try{
                    Hashtable hint=new Hashtable();
                    hint.put(EncodeHintType.CHARACTER_SET,"UTF-8");
                    BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE,200,200,hint);
                    bitMatrix=ChangeMask(bitMatrix);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    int a=6;
                    int b=a;
                    int pocz= 39-2;
                    int poczy=39-2;
                    try {
                        for (int o = poczy; o < bitMatrix.getHeight(); o += a) {
                            if ((o) % 6 == 0 && o != 37) {
                                for (int i = pocz; i < bitMatrix.getWidth(); i += b) {
                                    if (bitMatrix.get(i, o))
                                        bitMatrix.flip(i, o);
                                }
                            } else {
                                for (int i = pocz; i < bitMatrix.getWidth(); i++) {
                                    if (bitMatrix.get(i, o))
                                        bitMatrix.flip(i, o);
                                }
                            }
                        }
                        for (int i = pocz; i < bitMatrix.getWidth(); i += b) {
                            if ((i) % 6 == 0 && i != 37) {
                                for (int o = poczy; o < bitMatrix.getHeight(); o += a) {
                                    if (bitMatrix.get(i, o))
                                        bitMatrix.flip(i, o);
                                }
                            } else {
                                for (int o = poczy; o < bitMatrix.getHeight(); o++) {
                                    if (bitMatrix.get(i, o))
                                        bitMatrix.flip(i, o);
                                }
                            }
                        }

                    }catch(Exception e){
                        Toast.makeText(getApplicationContext(),"Error making white lines",Toast.LENGTH_SHORT).show();
                    }

                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    image.setImageBitmap(bitmap);

                }
                catch (WriterException e){
                    e.printStackTrace();
                }
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(text.getText().toString().trim().length()>10){
                    Toast.makeText(getApplicationContext(),"Please input correct data",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(text.getText().toString().trim()==text2Qr){
                        Toast.makeText(getApplicationContext(),"Correct",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"InCorrect",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
            }
        });
    }

}
