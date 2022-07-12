package com.example.myapplication.fragment;


import static com.example.myapplication.MainActivity.databaseHandler;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.model.History;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;

import com.example.myapplication.R;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GeneratorFragment extends Fragment {

    public GeneratorFragment() {
        // Required empty public constructor
    }

    private ImageView imgv_generated_code, imgv_close_popup, imgv_save_generated, imgv_clear_generated, imgv_share;
    private EditText edt_string_code, edt_img_name;
    private Button btn_generate_qr, btn_generate_bar, btn_final_save;
    private Bitmap bmp;
    private Boolean check_qrCode = false, check_barCode = false;
    private TextView tv_final_code_name;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_generator, container, false);

        imgv_generated_code = view.findViewById(R.id.imgv_generated_code);
        edt_string_code = view.findViewById(R.id.edt_string_code);
        imgv_save_generated = view.findViewById(R.id.imgv_save_generated);
        imgv_clear_generated = view.findViewById(R.id.imgv_clear_generated);
        btn_generate_qr = view.findViewById(R.id.btn_generate_qr);
        btn_generate_bar = view.findViewById(R.id.btn_generate_bar);
        imgv_share = view.findViewById(R.id.imgv_share);

        btn_generate_qr.setOnClickListener(view1 -> {
            if (edt_string_code.getText().toString().trim().length() == 0) {
                Toast.makeText(getActivity(), "Enter String!", Toast.LENGTH_SHORT).show();
            } else {
                databaseHandler.addHistory(new History(edt_string_code.getText().toString(), "QR_CODE", "Generate", getTime()));
                MultiFormatWriter writer = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = writer.encode(edt_string_code.getText().toString(), BarcodeFormat.QR_CODE, 500, 500);
                    int width = bitMatrix.getWidth();
                    int height = bitMatrix.getHeight();
                    bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                        }
                    }

                    imgv_generated_code.setImageBitmap(bmp);
                    imgv_save_generated.setVisibility(getView().VISIBLE);
                    imgv_clear_generated.setVisibility(getView().VISIBLE);
                    imgv_share.setVisibility(getView().VISIBLE);
                    check_qrCode = true;
                    check_barCode = false;

                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });

        btn_generate_bar.setOnClickListener(view12 -> {
            if (edt_string_code.getText().toString().trim().length() == 0) {
                Toast.makeText(getActivity(), "Enter String!", Toast.LENGTH_SHORT).show();
            } else {
                databaseHandler.addHistory(new History(edt_string_code.getText().toString(), "BARCODE", "Generate", getTime()));
                MultiFormatWriter writer = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = writer.encode(edt_string_code.getText().toString(), BarcodeFormat.CODE_128, 500, 500);
                    int width = bitMatrix.getWidth();
                    int height = bitMatrix.getHeight();
                    bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                        }
                    }

                    imgv_generated_code.setImageBitmap(bmp);
                    imgv_save_generated.setVisibility(getView().VISIBLE);
                    imgv_clear_generated.setVisibility(getView().VISIBLE);
                    imgv_share.setVisibility(getView().VISIBLE);
                    check_qrCode = false;
                    check_barCode = true;

                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });

        imgv_clear_generated.setOnClickListener(view13 -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(view13.getContext());
            alert.setTitle("Clear all");
            alert.setMessage("Are you sure you want to clear all?");
            alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // continue with delete
                    imgv_save_generated.setVisibility(getView().INVISIBLE);
                    imgv_clear_generated.setVisibility(getView().INVISIBLE);
                    imgv_share.setVisibility(getView().INVISIBLE);
                    edt_string_code.setText("");
                    imgv_generated_code.setImageResource(0);
                    bmp = null;
                    Toast.makeText(getActivity(), "Clear all!", Toast.LENGTH_SHORT).show();
                }
            });
            alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alert.show();
        });

        imgv_save_generated.setOnClickListener(view14 -> {
            View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.save_window, null);

            // create the popup window
            int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
            int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

            // show the popup window
            // which view you pass in doesn't matter, it is only used for the window tolken

            edt_img_name = popupView.findViewById(R.id.edt_image_name);
            btn_final_save = popupView.findViewById(R.id.btn_final_save);
            tv_final_code_name = popupView.findViewById(R.id.tv_final_name);
            imgv_close_popup = popupView.findViewById(R.id.imgv_pop_up_close);
            edt_img_name.setText(edt_string_code.getText().toString());
            if (check_barCode) {
                tv_final_code_name.setText(edt_string_code.getText().toString() + "_barCode.jpg");
            } else {
                tv_final_code_name.setText(edt_string_code.getText().toString() + "_QRcode.jpg");
            }

            edt_img_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (check_barCode) {
                        tv_final_code_name.setText(charSequence.toString() + "_barCode.jpg");
                    } else {
                        tv_final_code_name.setText(charSequence.toString() + "_QRcode.jpg");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            imgv_close_popup.setOnClickListener(view1412 -> popupWindow.dismiss());

            btn_final_save.setOnClickListener(view141 -> {
                if (edt_img_name.getText().toString().trim().length() != 0) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(view141.getContext());
                    alert.setTitle("Save generated code");
                    alert.setMessage("Are you sure you want to save as " + tv_final_code_name.getText().toString() + "?");
                    alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bmp, tv_final_code_name.getText().toString(), "");
                            Toast.makeText(getActivity(), "Saved!", Toast.LENGTH_SHORT).show();
                            popupWindow.dismiss();
                        }
                    });
                    alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // close dialog
                            dialog.cancel();
                        }
                    });
                    alert.show();
                } else {
                    Toast.makeText(getActivity(), "Enter image name!", Toast.LENGTH_SHORT).show();
                }
            });
            popupWindow.showAtLocation(view14, Gravity.CENTER, 0, 0);
        });

        imgv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) imgv_generated_code.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                shareImageandText(bitmap);
            }
        });
        return view;
    }

    private int tempCheck = getView().INVISIBLE;

    @Override
    public void onResume() {
        super.onResume();
        imgv_generated_code.setImageBitmap(bmp);
        imgv_save_generated.setVisibility(tempCheck);
        imgv_clear_generated.setVisibility(tempCheck);
        imgv_share.setVisibility(tempCheck);
    }

    @Override
    public void onPause() {
        super.onPause();
        tempCheck = imgv_save_generated.getVisibility();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getTime() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return myDateObj.format(myFormatObj);
    }

    private void shareImageandText(Bitmap bitmap) {
        Uri uri = getmageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        // putting uri of image to be shared
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        // setting type to image
        intent.setType("image/png");
        // calling startactivity() to share
        startActivity(Intent.createChooser(intent, "Share"));
    }

    // Retrieving the url to share
    private Uri getmageToShare(Bitmap bitmap) {
        File imagefolder = new File(getContext().getCacheDir(), "images");
        Uri uri = null;
        try {
            imagefolder.mkdirs();
            File file = new File(imagefolder, "shared_image.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(getContext(), "com.example.myapplication", file);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return uri;
    }
}