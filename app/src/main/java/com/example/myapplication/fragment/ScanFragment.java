package com.example.myapplication.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CLIPBOARD_SERVICE;

import static com.example.myapplication.MainActivity.check_fragment;
import static com.example.myapplication.MainActivity.databaseHandler;
import static com.example.myapplication.fragment.GeneratorFragment.getTime;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Any;
import com.example.myapplication.R;
import com.example.myapplication.model.History;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ScanFragment extends Fragment {

    private TextView tv_camera_scan_result;
    private static final String TAG = "ScanFragment";
    private ImageView imgv_camera, imgv_choose_image, imgv_copy;
    int SELECT_PICTURE = 111;
    Boolean check = false;

    public ScanFragment() {
        // Required empty public constructor
    }

    ActivityResultLauncher<ScanOptions> mStartForResult = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Intent originalIntent = result.getOriginalIntent();
            if (originalIntent == null) {
                Log.d(TAG, "Cancelled scan");
                Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
            } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                Log.d(TAG, "missing camera");
                Toast.makeText(getActivity(), "Cancel camera", Toast.LENGTH_SHORT).show();
            }
        } else {
            check = true;
            String type = result.getFormatName();
            if (type.equals("QR_CODE")) {
                databaseHandler.addHistory(new History(result.getContents(), "QR_CODE", "Scan", getTime()));
            } else {
                databaseHandler.addHistory(new History(result.getContents(), "BARCODE", "Scan", getTime()));
            }
            tv_camera_scan_result.setText(result.getContents());
            imgv_copy.setVisibility(getView().VISIBLE);
            String content = result.getContents();
            if (Patterns.WEB_URL.matcher(content).matches()) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(content));
                requireActivity().startActivity(i);
            }
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        tv_camera_scan_result = view.findViewById(R.id.tv_camera_scan_result);
        imgv_camera = view.findViewById(R.id.imgv_camera);
        imgv_choose_image = view.findViewById(R.id.imgv_choose_image);
        imgv_copy = view.findViewById(R.id.imgv_copy);

        imgv_choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(pickIntent, SELECT_PICTURE);
            }
        });

        imgv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanOptions options = new ScanOptions();
                options.setCaptureActivity(Any.class);
                options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
                options.setBeepEnabled(true);
                options.setPrompt("Scan something");
                options.setOrientationLocked(false);
                options.setCameraId(0);
                mStartForResult.launch(options);
            }
        });

        imgv_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", tv_camera_scan_result.getText());
                manager.setPrimaryClip(clipData);
                Toast.makeText(getActivity(), "Copied!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 111) {
                if (data == null || data.getData() == null) {
                    Log.e("TAG", "The uri is null, probably the user cancelled the image selection process using the back button.");
                    return;
                }
                //check_fragment = false;
                Uri uri = data.getData();
                String imagePath = uri.toString();
                Bundle result = new Bundle();
                result.putString("requestKey", imagePath);
                getParentFragmentManager().setFragmentResult("requestKey", result);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_screen, new ChooseImageFragment()).commit();
            }
        }
    }

    private int tempCheck = getView().INVISIBLE;
    private String tempString;

    @Override
    public void onResume() {
        super.onResume();
        //check_fragment = true;
        if (!check) {
            imgv_copy.setVisibility(tempCheck);
            tv_camera_scan_result.setText(tempString);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //check_fragment = true;
        if (check) {
            tempCheck = imgv_copy.getVisibility();
            tempString = tv_camera_scan_result.getText().toString();
            check = false;
        }
    }

}