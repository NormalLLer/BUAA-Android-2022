package com.example.success;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.success.entity.Label;
import com.example.success.entity.Word;
import com.example.success.translate.TranslateAPI;
import com.example.success.view.WheelView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Upload_Word extends Activity {

    private final int TAKE_PHOTO_ORC = 1;
    private final int CHOOSE_PHOTO_OCR = 2;
    private DatabaseInterface db = MainActivity.db;

    private final int CHOOSE_TXT = 3;
    private final int TAKE_PHOTO = 4;
    private final int CHOOSE_PHOTO = 5;
    private Uri imageUri;
    private int requestCode = 0;
    private ImageView picture;
    private EditText english_word;
    private EditText chinese;
    private WheelView wheelView;
    private int visible = View.INVISIBLE;
    private List<String> lists = new ArrayList<>();
    private EditText label1;
    //private EditText label2;
    private String oldEnglish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_upload_word);
        english_word = findViewById(R.id.english_word);
        chinese = findViewById(R.id.chinese_word);
        //TODO ??????????????????????????????
        lists.clear();
        lists.addAll(db.getAllLabel().stream().map
                (Label::getLabel).collect(Collectors.toList()));
//        for (int i = 0; i < 20; i++) {
//            lists.add("test:" + i);
//        }
        wheelView = (WheelView) findViewById(R.id.select_wheel2);
        if (lists.size() >= 3) {
            wheelView.lists(lists)
                    .fontSize(65)
                    .showCount(3)
                    .select(0)
                    .listener(new WheelView.OnWheelViewItemSelectListener() {
                        @Override
                        public void onItemSelect(int index) {
                            Log.d("cc", "current select:" + wheelView.getSelectItem() + " index :" + index + ",result=" + lists.get(index));
                        }
                    })
                    .build();
        } else {
            lists.add("??????");
            lists.add("??????");
            lists.add("??????");
            wheelView.lists(lists)
                    .fontSize(35)
                    .showCount(3)
                    .select(0)
                    .listener(new WheelView.OnWheelViewItemSelectListener() {
                        @Override
                        public void onItemSelect(int index) {
                            Log.d("cc", "current select:" + wheelView.getSelectItem() + " index :" + index + ",result=" + lists.get(index));
                        }
                    })
                    .build();
        }
        wheelView.setVisibility(visible);
        label1 = findViewById(R.id.word_label1);
        //label2 = findViewById(R.id.word_label2);
        picture = findViewById(R.id.image_help2);
        Long id = getIntent().getExtras().getLong("id");
        if (id != null) {
            String chinese = null;
            if ((chinese = getIntent().getExtras().getString("Chinese")) != null) {
                this.chinese.setText(chinese);
            }
            String english = null;
            if ((english = getIntent().getExtras().getString("English")) != null) {
                this.english_word.setText(english);
                oldEnglish = english;
            }
            byte[] image = null;
            if ((image = ShowTask.bytePicture) != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                this.picture.setImageBitmap(bitmap);
            }
            ArrayList<String> labels = null;
            if ((labels = getIntent().getExtras().getStringArrayList("label")) != null) {
                if(labels != null && labels.size() == 1) {
                    this.label1.setText(labels.get(0));
                }
            }
        }
    }

    public void select1(View view) {
        if (visible == View.INVISIBLE) {
            visible = View.VISIBLE;
        } else {
            visible = View.INVISIBLE;
            label1.setText(lists.get(wheelView.getSelectItem()));
        }
        wheelView.setVisibility(visible);
    }

//    public void select2(View view) {
//        if (visible == View.INVISIBLE) {
//            visible = View.VISIBLE;
//        } else {
//            visible = View.INVISIBLE;
//            label2.setText(lists.get(wheelView.getSelectItem()));
//        }
//        wheelView.setVisibility(visible);
//    }

    public void translate(View view) {
        String a = TranslateAPI.getChinese(english_word.getText().toString());
        if (a.length() > 70) {
            a = a.substring(0, 70);
            int i;
            for (i = 69; i >= 0; i--) {
                if (a.charAt(i) == '???' || a.charAt(i) == '???') {
                    break;
                }
            }
            a = a.substring(0, i + 1);
        }
        chinese.setText(a);
        if (a.equals("")) {
            Toast.makeText(Upload_Word.this, "????????????", Toast.LENGTH_SHORT).show();
        }
    }

    public void add_photo(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("??????");
        builder.setMessage("???????????????");
        builder.setPositiveButton("????????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestCode = CHOOSE_PHOTO;
                choosePhoto();
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestCode = TAKE_PHOTO;
                takePhoto();
            }
        });
        builder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (this.requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        //System.out.println("TAKE_PHOTO" + bitmap);
                        picture.setImageBitmap(bitmap);
                        //System.out.println("picture" + ((BitmapDrawable)picture.getDrawable()).getBitmap());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO_OCR:
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // ?????????????????????
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4?????????????????????????????????????????????
                        assert data != null;
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    public void takePhoto() {

        // ??????File???????????????????????????????????????????????????????????????SD?????????????????????????????????
        // ??????getExternalCacheDir()???????????????????????????

        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        System.out.println(getExternalCacheDir().toString());
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ?????????????????????????????????????????????Android 7.0?????????Uri???fromFile()?????????File???????????????Uri??????
        // ??????????????????FileProvider???getUriForFile()?????????File?????????????????????????????????Uri??????
        // ????????????????????????????????????????????????Context????????????????????????????????????????????????????????????File??????
        // FileProvider?????????????????????????????????????????????????????????????????????Uri???????????????????????????????????????????????????
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(this, "com.example.cameraalbumtest.fileprovider", outputImage);

        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        System.out.println("imageUri:::" + imageUri.toString());
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 2);
        } else {
            openCamera();
        }

        // ??????????????????
    }


    public void choosePhoto() {

        // ??????????????????????????????WRITE_EXTERNAL_STORAGE????????????????????????????????????????????????SD?????????????????????
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent, requestCode);
        }

    }

    private void openAlbum() {
        // ???intent???????????????????????????
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    public void chooseTxt(View view) {
        //????????????txt??????????????????
        Assets.extractAssets(Upload_Word.this);
        requestCode = CHOOSE_TXT;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            intoFileManager();
        }
    }

    private void intoFileManager() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");//???????????????
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    private void openCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;

            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {

        String imagePath = null;
        Uri uri = data.getData();

        if (DocumentsContract.isDocumentUri(this, uri)) {
            // ?????????document?????????Uri????????????document???id??????
            String docId = DocumentsContract.getDocumentId(uri);
            assert uri != null;
            // ??????Uri???authority???meida??????????????????document?????????????????????????????????????????????????????????????????????????????????id
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                // ????????????????????????id
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(docId));
                imagePath = getImagePath(contentUri, null);
            }

        } else {
            assert uri != null;
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                // ?????????content?????????uri????????????????????????
                imagePath = getImagePath(uri, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                // ?????????file?????????uri?????????????????????????????????
                imagePath = uri.getPath();
            }
        }
        if (BitmapFactory.decodeFile(imagePath) == null) {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        } else {
            picture.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        if (BitmapFactory.decodeFile(imagePath) == null) {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        } else {
            picture.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }
    }

    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // ??????Uri???selection??????????????????????????????
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public void save_word(View view) {
        //TODO ??????????????????
        int result;
        String word = english_word.getText().toString();
        String chinese = this.chinese.getText().toString();
        Bitmap bitmap = null;
        if (picture.getDrawable() != null) {
            bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        }
        System.out.println("bitmap" + bitmap);
        String labelContent = String.valueOf(label1.getText());
        //System.out.println("label:" + labelContent);
        Intent intent = getIntent();
        if (intent.getBooleanExtra("fromEdit", false)) {
            //TODO: ??????
            //System.out.println(bitmap);
//            byte[] data = new byte[3];
//            if (bitmap != null) {
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                data = baos.toByteArray();
//            }
            Intent ret = new Intent();
            //Bundle bundle = new Bundle();
            result = db.updateWord(MainActivity.name, oldEnglish, chinese, word, bitmap, labelContent);
            if(result == 0) {
                Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show();
            } else if (result == 2) {
                Toast.makeText(this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
            } else {
                setResult(RESULT_OK, ret);
                MainActivity.updateData();
                Toast.makeText(this, "???????????????", Toast.LENGTH_SHORT).show();
                finish();
            }


        } else {
            result = db.insertWord(MainActivity.name, chinese, word, bitmap);
            if(result == 3) {
                Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show();
            } else if (result == 0){
                Toast.makeText(this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
            } else{
                Word word1 = db.getWordByEnglish(MainActivity.name, word);
                db.wordAddLabel(word1, labelContent);
                MainActivity.updateData();
                Toast.makeText(this, "???????????????", Toast.LENGTH_SHORT).show();
                finish();
            }

        }

    }

    public void back2(View view) {
        finish();
    }
}