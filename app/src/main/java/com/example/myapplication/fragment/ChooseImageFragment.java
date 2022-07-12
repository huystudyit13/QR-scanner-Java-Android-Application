package com.example.myapplication.fragment;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.app.Activity.RESULT_OK;

import static com.example.myapplication.MainActivity.check_fragment;
import static com.example.myapplication.MainActivity.databaseHandler;
import static com.example.myapplication.fragment.GeneratorFragment.getTime;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.History;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ChooseImageFragment extends Fragment {
    ImageView imgv_back, imgv_choose_image, imgv_preview, imgv_copy, imgv_share;
    TextView tv_scanned;
    CardView cv_scanned_field;
    int SELECT_PICTURE = 111;

    public ChooseImageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_image, container, false);
        imgv_back = view.findViewById(R.id.imgv_back);
        imgv_preview = view.findViewById(R.id.imgv_preview_image);
        tv_scanned = view.findViewById(R.id.tv_scanned);
        cv_scanned_field = view.findViewById(R.id.scanned_field);
        imgv_copy = view.findViewById(R.id.imgv_copy);
        imgv_choose_image = view.findViewById(R.id.imgv_choose_image);
        imgv_share = view.findViewById(R.id.imgv_share);

        imgv_back.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                //check_fragment = true;
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_screen, new ScanFragment()).commit();
            }
        });

        imgv_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", tv_scanned.getText());
                manager.setPrimaryClip(clipData);
                Toast.makeText(getActivity(), "Copied!", Toast.LENGTH_SHORT).show();
            }
        });

        imgv_choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv_scanned_field.setVisibility(getView().INVISIBLE);
                tv_scanned.setVisibility(getView().INVISIBLE);
                imgv_copy.setVisibility(getView().INVISIBLE);
                imgv_share.setVisibility(getView().INVISIBLE);
                imageChooser();
            }
        });

        imgv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) imgv_preview.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                shareImageandText(bitmap);
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
                String path = bundle.getString("requestKey");
                // Do something with the result
                Uri uri = Uri.parse(path);
                imgv_preview.setImageURI(uri);
                try {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    int width = bitmap.getWidth(), height = bitmap.getHeight();
                    int[] pixels = new int[width * height];
                    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                    bitmap.recycle();
                    bitmap = null;
                    RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                    BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
                    MultiFormatReader reader = new MultiFormatReader();
                    try {
                        Result result = reader.decode(bBitmap);
                        tv_scanned.setText(result.getText());
                        if (Patterns.WEB_URL.matcher(tv_scanned.getText()).matches()) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(tv_scanned.getText().toString()));
                            requireActivity().startActivity(i);
                        }
                        if (result.getBarcodeFormat().toString().equals("CODE_128")) {
                            databaseHandler.addHistory(new History(tv_scanned.getText().toString(), "BARCODE", "Scan", getTime()));
                        } else {
                            databaseHandler.addHistory(new History(tv_scanned.getText().toString(), "QR_CODE", "Scan", getTime()));
                        }

                        cv_scanned_field.setVisibility(getView().VISIBLE);
                        tv_scanned.setVisibility(getView().VISIBLE);
                        imgv_copy.setVisibility(getView().VISIBLE);
                        imgv_share.setVisibility(getView().VISIBLE);

                    } catch (NotFoundException e) {
                        Log.e("TAG", "decode exception", e);
                        Toast.makeText(getActivity(), "This image is not a QR code: ", Toast.LENGTH_SHORT).show();
                    }
                } catch (FileNotFoundException e) {
                    Log.e("TAG", "can not open file" + uri.toString(), e);
                }
            }
        });
    }

    void imageChooser() {
        tv_scanned.setVisibility(getView().INVISIBLE);
        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pickIntent, SELECT_PICTURE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 111) {
                if (data == null || data.getData() == null) {
                    Log.e("TAG", "The uri is null, probably the user cancelled the image selection process using the back button.");
                    return;
                }
                Uri uri = data.getData();
                imgv_preview.setImageURI(uri);
                try {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (bitmap == null) {
                        return;
                    }
                    int width = bitmap.getWidth(), height = bitmap.getHeight();
                    int[] pixels = new int[width * height];
                    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                    bitmap.recycle();
                    bitmap = null;
                    RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                    BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
                    MultiFormatReader reader = new MultiFormatReader();
                    try {
                        Result result = reader.decode(bBitmap);
                        tv_scanned.setText(result.getText());
                        if (Patterns.WEB_URL.matcher(tv_scanned.getText()).matches()) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(tv_scanned.getText().toString()));
                            requireActivity().startActivity(i);
                        }
                        if (result.getBarcodeFormat().toString().equals("CODE_128")) {
                            databaseHandler.addHistory(new History(tv_scanned.getText().toString(), "BARCODE", "Scan", getTime()));
                        } else {
                            databaseHandler.addHistory(new History(tv_scanned.getText().toString(), "QR_CODE", "Scan", getTime()));
                        }

                        cv_scanned_field.setVisibility(getView().VISIBLE);
                        tv_scanned.setVisibility(getView().VISIBLE);
                        imgv_copy.setVisibility(getView().VISIBLE);
                        imgv_share.setVisibility(getView().VISIBLE);

                    } catch (NotFoundException e) {
                        Log.e("TAG", "decode exception", e);
                        Toast.makeText(getActivity(), "This image is not a QR code: ", Toast.LENGTH_SHORT).show();

                    }
                } catch (FileNotFoundException e) {
                    Log.e("TAG", "can not open file" + uri.toString(), e);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        //check_fragment = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStop() {
        super.onStop();
        //check_fragment = false;
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