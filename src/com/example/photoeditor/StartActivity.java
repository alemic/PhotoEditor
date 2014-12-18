package com.example.photoeditor;import android.app.Activity;import android.content.ContentResolver;import android.content.Context;import android.content.Intent;import android.content.res.AssetFileDescriptor;import android.database.Cursor;import android.graphics.Bitmap;import android.graphics.BitmapFactory;import android.graphics.Matrix;import android.graphics.drawable.BitmapDrawable;import android.net.Uri;import android.os.Bundle;import android.os.ParcelFileDescriptor;import android.provider.MediaStore;import android.support.v4.app.FragmentActivity;import android.view.*;import android.view.animation.Animation;import android.view.animation.AnimationUtils;import android.widget.*;import com.capricorn.RayMenu;import com.edmodo.cropper.CropImageView;import com.example.photoeditor.fragments.CropDialogFragment;import com.example.photoeditor.fragments.DisplayPhotoFragment;import com.example.photoeditor.fragments.SaveDialogFragment;import com.example.photoeditor.helpers.*;import com.example.photoeditor.interfaces.CropResultListener;import java.io.File;import java.io.FileDescriptor;import java.io.FileNotFoundException;import java.io.FileOutputStream;import java.net.URI;public class StartActivity extends FragmentActivity implements CropResultListener {	public static final String PHOTO_FROM_EDITOR = "choosen_photo",TAG="show_dialog",IMAGE_FILE_NAME="photo.jpg";	public static final int INSTAGRAM = 28,DEFAULT_SCALE= 10,MAX_OPACITY = 255,CAMERA=29,GALLERY=30,FILTER = 31,	FUNNY= 32,PADDING_VALUE= 50;	private boolean mIsBehind = false;	private FrameLayout mContainer;	private RayMenu mArcMenu;	private ImageView mPhotoImageView,mBehindImageView;	private float mScaleValue = 1;	private FrameLayout.LayoutParams mImageViewParams,mBehindImageViewParams;	private SeekBar mSeekBarOpacityChange,mSeekBarScaleChange;	private Bitmap mPhotoBitmap,mBehindPhotoBitmap;	private RelativeLayout.LayoutParams mContainerParams;	private Animation.AnimationListener animationListener = new Animation.AnimationListener() {		@Override		public void onAnimationStart(Animation animation) {		}		@Override		public void onAnimationEnd(Animation animation) {			mContainer.startAnimation(AnimationUtils.loadAnimation(StartActivity.this,R.anim.change_second));			mPhotoImageView.setImageAlpha(MAX_OPACITY);			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)mPhotoImageView.getLayoutParams();			int top = params.topMargin;			int left = params.leftMargin;			mBehindImageViewParams = (FrameLayout.LayoutParams)mBehindImageView.getLayoutParams();			mBehindImageViewParams.leftMargin = left;			mBehindImageViewParams.topMargin = top;			mBehindImageView.setLayoutParams(mBehindImageViewParams);			replaceImages();			setDefaultProgress();			resizeToContent();		}		@Override		public void onAnimationRepeat(Animation animation) {		}	};    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_start);		mContainer = (FrameLayout)findViewById(R.id.photo_container);		mArcMenu = (RayMenu)findViewById(R.id.arc_menu);		mPhotoImageView =(ImageView)findViewById(R.id.photoImageView);		mBehindImageView = (ImageView)findViewById(R.id.behindImageView);		mSeekBarOpacityChange = (SeekBar)findViewById(R.id.seekBarOpacityChange);		mSeekBarScaleChange = (SeekBar)findViewById(R.id.seekBarScale);		mContainerParams = (RelativeLayout.LayoutParams)mContainer.getLayoutParams();		mImageViewParams = (FrameLayout.LayoutParams)mPhotoImageView.getLayoutParams();		mBehindImageViewParams =(FrameLayout.LayoutParams)mBehindImageView.getLayoutParams();		registerForContextMenu(mContainer);		setEnabled(false,mSeekBarOpacityChange,mSeekBarScaleChange);        BindHandlers();		initArcMenu();    }    private void BindHandlers()    {		mPhotoImageView.setOnTouchListener(new View.OnTouchListener() {			private float startX = 0,startY = 0;			@Override			public boolean onTouch(View view, MotionEvent motionEvent) {				switch (motionEvent.getActionMasked()) {					case MotionEvent.ACTION_DOWN:						mImageViewParams = (FrameLayout.LayoutParams) mPhotoImageView.getLayoutParams();						startX = motionEvent.getX();						startY = motionEvent.getY();						break;					case MotionEvent.ACTION_MOVE:						float left = (motionEvent.getX()-startX) * 0.3f;						float top = (motionEvent.getY()-startY) * 0.3f;						mImageViewParams.leftMargin +=left;						mImageViewParams.topMargin += top;					        mPhotoImageView.setLayoutParams(mImageViewParams);						break;					case MotionEvent.ACTION_UP:						break;				}				return true;			}});			mSeekBarOpacityChange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {				@Override				public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {					mPhotoImageView.setImageAlpha(progress);				}				@Override				public void onStartTrackingTouch(SeekBar seekBar) {				}				@Override				public void onStopTrackingTouch(SeekBar seekBar) {				}			});		mSeekBarScaleChange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {			@Override			public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {				mScaleValue = progress/10f;				imageScale();			}			@Override			public void onStartTrackingTouch(SeekBar seekBar) {			}			@Override			public void onStopTrackingTouch(SeekBar seekBar) {			}		});	}    @Override	protected void onActivityResult(int requestCode, int resultCode, Intent data){		if(resultCode!=Activity.RESULT_OK)		{			return;		}		Bitmap photo = null;		String fileImageName =null;		switch (requestCode) {			case INSTAGRAM:				int position = data.getIntExtra(DisplayPhotoFragment.CHOOSEN_PHOTO,-1);				ImageDowloader dowloader = new ImageDowloader();				dowloader.execute(ImagesRepository.getPhotos("","").getmBitmaps().get(position));				while (!(dowloader.isSuccess()));				photo = dowloader.getmPhoto();				break;			case CAMERA:				byte[]bitmapArray = data.getByteArrayExtra(CameraActivity.RESULT_PHOTO_FROM_CAMERA);				photo = BitmapFactory.decodeByteArray(bitmapArray,0,bitmapArray.length);				break;			case GALLERY:				Uri uriImage = data.getData();				try {					if(CacheHelper.alreadyInCache(uriImage.toString()))					{						photo = CacheHelper.getBitmap(uriImage.toString());					}					else					{						ParcelFileDescriptor.AutoCloseInputStream stream = (ParcelFileDescriptor.AutoCloseInputStream)getContentResolver().openInputStream(uriImage);						photo = Drawing.decodeSampledBitmapFromFileDescriptor(stream.getFD());						CacheHelper.addBitmap(photo,uriImage.toString());						mPhotoImageView.invalidate();					}				} catch (Exception e) {					e.printStackTrace();				}				break;			case FILTER:				fileImageName = data.getStringExtra(FilterActivity.PHOTO_WITH_EFFECT);				photo = ImageSaver.getImageInPrivateRepository(this, fileImageName);				break;			case FUNNY:				fileImageName = data.getStringExtra(FunnyActivity.PHOTO_WITH_FUNNY);				photo = ImageSaver.getImageInPrivateRepository(this, fileImageName);				break;			default:				super.onActivityResult(requestCode, resultCode,data);				break;		}		setImageBitmap(mIsBehind,photo);	}	private void activityStart(Context context,Class activityClass,int requestCode)	{		Intent intent = new Intent(context, activityClass);		startActivityForResult(intent, requestCode);	}	private void replaceImages()	{		acceptChanges();		Bitmap bitmap = mBehindPhotoBitmap;		mBehindPhotoBitmap = mPhotoBitmap;		mPhotoBitmap = bitmap;		mPhotoImageView.setImageBitmap(mPhotoBitmap);		mBehindImageView.setImageBitmap(mBehindPhotoBitmap);	}	private void acceptChanges()	{		if(mSeekBarOpacityChange.getProgress()!=MAX_OPACITY) {			setImageBitmapWithOpacity(mPhotoBitmap, mSeekBarOpacityChange.getProgress());		}		if(mSeekBarScaleChange.getProgress()!=DEFAULT_SCALE) {			mPhotoBitmap = Bitmap.createScaledBitmap(mPhotoBitmap, (int) (mPhotoBitmap.getWidth() * mScaleValue),					(int) (mPhotoBitmap.getHeight() * mScaleValue), false);		}	}	private void setImageBitmap(boolean isBehind,Bitmap bitmap)	{		if(isBehind)		{			mBehindImageView.setImageBitmap(bitmap);			mBehindPhotoBitmap = bitmap;			mBehindImageViewParams.leftMargin = 0;			mBehindImageViewParams.topMargin = 0;			mBehindImageView.setLayoutParams(mBehindImageViewParams);			resizeToContent();		}		else		{			mPhotoImageView.setImageBitmap(bitmap);			mPhotoBitmap = bitmap;			openRayMenu();			mIsBehind = true;			resizeToContent();			setDefaultProgress();			setEnabled(true,mSeekBarScaleChange,mSeekBarOpacityChange);		}	}	private void setDefaultProgress()	{		mSeekBarOpacityChange.setProgress(mSeekBarOpacityChange.getMax());		mSeekBarScaleChange.setProgress(DEFAULT_SCALE);		mImageViewParams.leftMargin = 0;		mImageViewParams.topMargin = 0;		mPhotoImageView.setLayoutParams(mImageViewParams);		Matrix matrix = mPhotoImageView.getImageMatrix();		matrix.setScale((DEFAULT_SCALE/10),(DEFAULT_SCALE/10));	}	private void clearImageView(ImageView ...views)	{		for(ImageView view:views)		{			view.setImageBitmap(null);		}	}	private void resizeToContent()	{		if(mBehindPhotoBitmap!=null) {			mContainerParams.width = mBehindPhotoBitmap.getWidth() > mPhotoBitmap.getWidth() ?					mBehindPhotoBitmap.getWidth()+PADDING_VALUE : mPhotoBitmap.getWidth()+PADDING_VALUE;			mContainerParams.height = mBehindPhotoBitmap.getHeight() > mPhotoBitmap.getHeight() ?					mBehindPhotoBitmap.getHeight()+PADDING_VALUE : mPhotoBitmap.getHeight()+PADDING_VALUE;		}		else		{			mContainerParams.width = mPhotoBitmap.getWidth()+PADDING_VALUE;			mContainerParams.height = mPhotoBitmap.getHeight()+PADDING_VALUE;		}		mImageViewParams.height = mPhotoBitmap.getHeight();		mImageViewParams.width = mPhotoBitmap.getWidth();		mPhotoImageView.setLayoutParams(mImageViewParams);		mContainer.setLayoutParams(mContainerParams);	}	private void resizeToContent(ViewGroup.LayoutParams params)	{		mContainerParams.height = (params.height+PADDING_VALUE);		mContainerParams.width = (params.width+PADDING_VALUE);	}	private void setImageBitmapWithOpacity(Bitmap originalBitmap,int opacity)	{		Bitmap bitmap = new Drawing().setBitmapOpacity(originalBitmap,opacity);		mPhotoImageView.setImageBitmap(bitmap);		mPhotoBitmap = bitmap;	}	private void openRayMenu()	{		MotionEvent event = MotionEvent.obtain(0,0,MotionEvent.ACTION_DOWN,0,0,0);		mArcMenu.getContainer().dispatchTouchEvent(event);	}	@Override	public boolean onCreateOptionsMenu(Menu menu) {		MenuInflater inflater = getMenuInflater();		inflater.inflate(R.menu.main_options_menu,menu);		return true;	}	@Override	public boolean onMenuItemSelected(int featureId, MenuItem item) {		switch (item.getItemId())		{			case R.id.save:				if(mPhotoBitmap!=null) {					setImageBitmapWithOpacity(mPhotoBitmap, mPhotoImageView.getImageAlpha());					SaveDialogFragment dialogFragment = SaveDialogFragment.newInstance(mPhotoBitmap);					dialogFragment.show(getSupportFragmentManager(),TAG);				}				return true;			case R.id.menu:				openContextMenu(mContainer);				return true;			case R.id.crop:				if(mPhotoBitmap!=null) {					ImageSaver.saveImageInPrivateRepository(this,mPhotoBitmap,IMAGE_FILE_NAME);					CropDialogFragment.newInstance(IMAGE_FILE_NAME).show(getSupportFragmentManager(), "bla");				}				return true;			default:				return super.onMenuItemSelected(featureId,item);		}	}	@Override	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {		MenuInflater inflater = getMenuInflater();		inflater.inflate(R.menu.main_context_menu,menu);	}	@Override	public boolean onContextItemSelected(MenuItem item) {		switch (item.getItemId()) {			case R.id.instagram:				activityStart(StartActivity.this, InstagramActivity.class, INSTAGRAM);				return true;			case R.id.gallery:				Intent galaryIntent = new Intent(Intent.ACTION_GET_CONTENT);				galaryIntent.setType("image/*");				startActivityForResult(galaryIntent, GALLERY);				return true;			case R.id.camera:				activityStart(this, CameraActivity.class, CAMERA);				mIsBehind = false;				return true;			case R.id.filter:				if(mPhotoBitmap!=null) {					Intent intent = new Intent(this, FilterActivity.class);					intent.putExtra(PHOTO_FROM_EDITOR,IMAGE_FILE_NAME);					ImageSaver.saveImageInPrivateRepository(this,mPhotoBitmap,IMAGE_FILE_NAME);					startActivityForResult(intent, FILTER);					mIsBehind = false;					return true;				}			case R.id.funny:				ImageSaver.saveImageInPrivateRepository(this,mPhotoBitmap,IMAGE_FILE_NAME);				Intent intent = new Intent(this, FunnyActivity.class);				intent.putExtra(PHOTO_FROM_EDITOR, IMAGE_FILE_NAME);				startActivityForResult(intent,FUNNY);				mIsBehind = false;		}	            return false;	}	private void initArcMenu()	{		int[]sources = {R.drawable.change,R.drawable.join,R.drawable.clear,R.drawable.left_rigth,R.drawable.top_bottom};		View.OnClickListener[]listeners =				{						new View.OnClickListener() {							@Override							public void onClick(View view) {								if(mBehindPhotoBitmap!=null) {									Animation changeAnimation = AnimationUtils.loadAnimation(StartActivity.this, R.anim.change);									changeAnimation.setAnimationListener(animationListener);									mContainer.startAnimation(changeAnimation);								}							}						},						new View.OnClickListener() {							@Override							public void onClick(View view) {								if(mBehindPhotoBitmap!=null) {									acceptChanges();									int left = Drawing.getDifferent(mBehindImageViewParams.leftMargin, mImageViewParams.leftMargin);									int top = Drawing.getDifferent(mBehindImageViewParams.topMargin, mImageViewParams.topMargin);									Bitmap bitmap = new Drawing().putOverlay(mBehindPhotoBitmap, mPhotoBitmap, top, left);									mPhotoBitmap = bitmap;									mPhotoImageView.setImageBitmap(bitmap);									clearImageView(mBehindImageView);									mBehindPhotoBitmap = null;									setDefaultProgress();									resizeToContent();								}							}						},						new View.OnClickListener() {							@Override							public void onClick(View view) {								clearImageView(mPhotoImageView,mBehindImageView);								mIsBehind = false;								mBehindPhotoBitmap = null;								mPhotoBitmap = null;								setEnabled(false,mSeekBarOpacityChange,mSeekBarScaleChange);							}						},						new View.OnClickListener() {							@Override							public void onClick(View view) {								if(mBehindPhotoBitmap!=null) {									mScaleValue = ((float) mBehindImageView.getWidth() / (float) mPhotoImageView.getWidth());									imageScale();									resizeToContent(mImageViewParams);								}							}						},						new View.OnClickListener() {							@Override							public void onClick(View view) {								if(mBehindPhotoBitmap!=null) {									mScaleValue = ((float) mBehindImageView.getHeight() / (float) mPhotoImageView.getHeight());									imageScale();									resizeToContent(mImageViewParams);								}							}						}				};		 new Drawing().arcMenuInit(mArcMenu,StartActivity.this,listeners,sources);	}	private void setEnabled(boolean enable,View ... views)	{		for(View view:views)		{			view.setEnabled(enable);		}	}	private void imageScale()	{		Matrix matrix = mPhotoImageView.getImageMatrix();		matrix.setScale(mScaleValue,mScaleValue);		mPhotoImageView.setImageMatrix(matrix);		mImageViewParams = (FrameLayout.LayoutParams)mPhotoImageView.getLayoutParams();		mImageViewParams.height= (int)(mPhotoBitmap.getHeight()*mScaleValue);		mImageViewParams.width = (int)(mPhotoBitmap.getWidth()*mScaleValue);		mPhotoImageView.setLayoutParams(mImageViewParams);	}	@Override	public void onCropResult(CropResults result,Intent data) {		switch (result)		{			case Ok:				mPhotoBitmap = ImageSaver.getImageInPrivateRepository(this,IMAGE_FILE_NAME);				mPhotoImageView.setImageBitmap(mPhotoBitmap);				resizeToContent();				break;			case Cancel:				break;			default:				break;		}	}}