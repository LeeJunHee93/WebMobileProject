package kr.ac.kumoh.ce.mobile.sportsgo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapCalloutCustomOverlay;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

import java.util.ArrayList;

/**
 * Created by dlgus on 2017-04-29.
 */
public class MainPage extends Fragment {
    View rootView;
    TextView tx1, tx2;
    int  flag = 0;

    final ArrayList itemSelected = new ArrayList();
    private FloatingActionButton fabMainPage;

    private NMapContext mMapContext;
    private static final String CLIENT_ID = "uXlyvf1k4B27uW1FU0u2";// 애플리케이션 클라이언트 아이디 값

    private static final String LOG_TAG = "NMapViewer";
    private static final boolean DEBUG = false;

    NMapView mapView;
    private NMapController mMapController;

    private NMapOverlayManager mOverlayManager;

    private NMapMyLocationOverlay mMyLocationOverlay;
    private NMapLocationManager mMapLocationManager;
    private NMapCompassManager mMapCompassManager;

    private NMapViewerResourceProvider mMapViewerResourceProvider;

    private NMapPOIdataOverlay mFloatingPOIdataOverlay;
    private NMapPOIitem mFloatingPOIitem;

    private MapContainerView mMapContainerView;
    private NMapContext a;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapView = (NMapView)getView().findViewById(R.id.mapView);
        mapView.setClientId(CLIENT_ID);// 클라이언트 아이디 설정
        mapView.setClickable(true);
        mapView.setEnabled(true);
        mapView.setFocusable(true);
        mapView.setFocusableInTouchMode(true);
        mapView.requestFocus();
        mMapContext.setupMapView(mapView);

        mapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
        mapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);
        mapView.setOnMapViewDelegate(onMapViewTouchDelegate);

        // use map controller to zoom in/out, pan and set map center, zoom level etc.
        mMapController = mapView.getMapController();

        // use built in zoom controls
        NMapView.LayoutParams lp = new NMapView.LayoutParams(NMapView.LayoutParams.WRAP_CONTENT,
                NMapView.LayoutParams.WRAP_CONTENT, NMapView.LayoutParams.BOTTOM_RIGHT);

        // 확대/축소를 위한 줌 컨트롤러 표시 옵션 활성화
        mapView.setBuiltInZoomControls(true, lp);

        // 오버레이 리소스 관리객체 할당 create resource provider
        mMapViewerResourceProvider = new NMapViewerResourceProvider(this.getActivity());
        // set data provider listener
        setMapDataProviderListener(onDataProviderListener);

        // create overlay manager
        // 지도위에 표시되는 오버레이 객체들을 관리
        mOverlayManager = new NMapOverlayManager(this.getActivity(), mapView, mMapViewerResourceProvider);
        // register callout overlay listener to customize it.
        mOverlayManager.setOnCalloutOverlayListener(onCalloutOverlayListener);
        // register callout overlay view listener to customize it.
        mOverlayManager.setOnCalloutOverlayViewListener(onCalloutOverlayViewListener);

        // location manager
        mMapLocationManager = new NMapLocationManager(this.getActivity());
        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);

        // compass manager
        mMapCompassManager = new NMapCompassManager(this.getActivity());

        mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapContext =  new NMapContext(super.getActivity());
        mMapContext.onCreate();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.mainpage_main, container, false);
        tx1 = (TextView)rootView.findViewById(R.id.textView1);
        tx1.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/newTown.ttf"));
        tx2 = (TextView)rootView.findViewById(R.id.textView2);
        tx2.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/newTown.ttf"));

        fabMainPage = (FloatingActionButton)rootView.findViewById(R.id.fabMainpage);
        fabMainPage.setOnClickListener(clickListener);

        return rootView;
    }
    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fabMainpage:
                    final String[] items = {"축구", "농구", "풋살", "배드민턴"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("종목을 선택하세요")
                            .setMultiChoiceItems(items, null,
                                    new DialogInterface.OnMultiChoiceClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                            tx1.setText("");
                                            if (isChecked) {
                                                if(itemSelected.size() > 2) {
                                                    Toast.makeText(getContext(), "최대 3개가지 선택가능합니다.", Toast.LENGTH_SHORT).show();
                                                    ((AlertDialog)dialog).getListView().setItemChecked(which, false);
                                                }
                                                else
                                                    itemSelected.add(which);
                                            }
                                            else if (itemSelected.contains(which))
                                                itemSelected.remove(Integer.valueOf(which));
                                        }
                                    })
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for(int i=0; i<itemSelected.size(); i++)
                                        tx1.setText(tx1.getText() + "   " + items[(Integer) itemSelected.get(i)]);
                                    POIdataOverlay(itemSelected);
                                    itemSelected.clear();
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    itemSelected.clear();
                                }
                            });
                    builder.create();
                    builder.show();
                    break;
            }
        }
    };
    int A[] = {6, 5, 5, 6}; // 종목별 마커수
    double sport[][][] = { // 종목별 경도위도 좌표
            {
                    {128.387908, 36.145690},
                    {128.396855, 36.135888},
                    {128.438515, 36.135044},
                    {128.382141, 36.120216},
                    {128.332281, 36.103716},
                    {128.426904, 36.101822}
            },
            {
                    {128.394140, 36.147655},
                    {128.403098, 36.135069},
                    {128.416344, 36.145152},
                    {128.392735, 36.115331},
                    {128.434075, 36.118279}
            },
            {
                    {128.389096, 36.145950},
                    {128.403668, 36.136911},
                    {128.411407, 36.138479},
                    {128.374276, 36.125174},
                    { 128.399383,36.084937}
            },
            {
                    {128.387908, 36.145690},
                    {128.395454, 36.145299},
                    {128.368365, 36.118013},
                    {128.431263, 36.142566},
                    {128.401353, 36.101382},
                    {128.336073, 36.113804}

            },
    };

    String Ssport1[][] = { // 종목별 구장이름
            {"옥계축구연합", "구미축구협회", "강변축구장", "유소년축구클럽", "슛돌이축구장", "호날두축구장"},
            {"금오농구클럽", "인동농구장", "구미농구협회", "한마음농구교실", "커리농구장"},
            {"두발로풋살장", "강변풋살장", "유소년축구클럽", "은봉풋살경기장", "월드풋살클럽"},
            {"구미배드민턴", "봉배드민턴장", "배드민턴플러스", "자이언트배드민턴", "배드민턴매니아", "금오배드민턴클럽"}
    };
    String Address[][] = {
            {
                    "경상북도 구미시 양포동 금오공과대학교 운동장",
                    "경상북도 구미시 거의동 365-4",
                    "경상북도 구미시 양포동 137-3",
                    "경상북도 구미시 비산동 264",
                    "경상북도 구미시 형곡2동 산38",
                    "경상북도 구미시 인동13길 23"
            },
            {
                    "경상북도 구미시 거의동 산13",
                    "경상북도 구미시 양포동 929-1",
                    "경상북도 구미시 양포동 산117",
                    "경상북도 구미시 공단1동 산4",
                    "경상북도 구미시 황상동 97-3"
            },
            {
                    "경상북도 구미시 양포동 2",
                    "경상북도 구미시 거의동 산86-4",
                    "경상북도 구미시 거의동 610-1",
                    "경상북도 구미시 비산동 413",
                    "경상북도 칠곡군 석적읍 중리 495-5"
            },
            {
                    "경상북도 구미시 양포동 금오공과대학교 체육관",
                    "경상북도 구미시 거의동 산49",
                    "경상북도 구미시 공단1동 331-15",
                    "경상북도 구미시 산동면 신당리 1192-10",
                    "경상북도 구미시 진평동 647",
                    "경상북도 구미시 경은로 85"
            },

    };

    String Call[][] = {
            {
                    "010-1856-6215",
                    "054-545-8523",
                    "010-1235-8456",
                    "054-321-6548",
                    "010-4862-9978",
                    "010-8853-7410"
            },
            {
                    "054-126-4574",
                    "010-3756-3335",
                    "010-4568-1239",
                    "054-985-6571",
                    "054-632-1238"
            },
            {
                    "010-8523-9476",
                    "010-8462-5195",
                    "010-9713-1323",
                    "054-540-3088",
                    "054-850-2254"
            },
            {
                    "054-630-8541",
                    "010-1035-6825",
                    "010-9630-5412",
                    "054-552-6696",
                    "010-7749-3321",
                    "010-3005-6256"
            },
    };
    private void POIdataOverlay(ArrayList itemSelected) {
        mOverlayManager.clearOverlays();

        int markerId[] = {NMapPOIflagType.PIN, NMapPOIflagType.PIN, NMapPOIflagType.PIN,NMapPOIflagType.PIN} ;
        int markernum = 0;

        for(int i=0; i<itemSelected.size(); i++) {
            markerId[i] = markerId[i] + (Integer) itemSelected.get(i);
            markernum += A[(Integer) itemSelected.get(i)];

        }
        NMapPOIdata poiData = new NMapPOIdata(markernum, mMapViewerResourceProvider);

        poiData.beginPOIdata(markernum);
        for(int i=0; i<itemSelected.size(); i++) {
            for(int j=0; j<A[(Integer) itemSelected.get(i)]; j++) {
                poiData.addPOIitem(sport[(Integer) itemSelected.get(i)][j][0], sport[(Integer) itemSelected.get(i)][j][1], Ssport1[(Integer) itemSelected.get(i)][j], markerId[i], 0);
            }
        }
        poiData.endPOIdata();

        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
        poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);
        poiDataOverlay.selectPOIitem(0, true);
    }

    @Override
    public void onStart(){
        super.onStart();
        mMapContext.onStart();
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapContext.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mMapContext.onPause();
    }
    @Override
    public void onStop() {
        mMapContext.onStop();
        super.onStop();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void onDestroy() {
        mMapContext.onDestroy();
        super.onDestroy();
    }

    private void startMyLocation() {

        if (mMyLocationOverlay != null) {
            if (!mOverlayManager.hasOverlay(mMyLocationOverlay)) {
                mOverlayManager.addOverlay(mMyLocationOverlay);
            }

            if (mMapLocationManager.isMyLocationEnabled()) {

                if (!mapView.isAutoRotateEnabled()) {
                    mMyLocationOverlay.setCompassHeadingVisible(true);

                    mMapCompassManager.enableCompass();

                    mapView.setAutoRotateEnabled(true, false);

                    mMapContainerView.requestLayout();
                } else {
                    stopMyLocation();
                }

                mapView.postInvalidate();
            } else {
                boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(true);
                if (!isMyLocationEnabled) {
                    Toast.makeText(getContext(), "Please enable a My Location source in system settings",
                            Toast.LENGTH_LONG).show();

                    Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(goToSettings);

                    return;
                }
            }
        }
    }
    private void stopMyLocation() {
        if (mMyLocationOverlay != null) {
            mMapLocationManager.disableMyLocation();

            if (mapView.isAutoRotateEnabled()) {
                mMyLocationOverlay.setCompassHeadingVisible(false);

                mMapCompassManager.disableCompass();

                mapView.setAutoRotateEnabled(false, false);

                mMapContainerView.requestLayout();
            }
        }
    }


    public void setMapDataProviderListener(NMapActivity.OnDataProviderListener var1) {
        if(this.a != null) {
            this.a.setMapDataProviderListener(var1);
        }

    }

    private final NMapActivity.OnDataProviderListener onDataProviderListener = new NMapActivity.OnDataProviderListener() {

        @Override
        public void onReverseGeocoderResponse(NMapPlacemark placeMark, NMapError errInfo) {

            if (DEBUG) {
                Log.i(LOG_TAG, "onReverseGeocoderResponse: placeMark="
                        + ((placeMark != null) ? placeMark.toString() : null));
            }

            if (errInfo != null) {
                Log.e(LOG_TAG, "Failed to findPlacemarkAtLocation: error=" + errInfo.toString());

                Toast.makeText(getContext(), errInfo.toString(), Toast.LENGTH_LONG).show();
                return;
            }

            if (mFloatingPOIitem != null && mFloatingPOIdataOverlay != null) {
                mFloatingPOIdataOverlay.deselectFocusedPOIitem();

                if (placeMark != null) {
                    mFloatingPOIitem.setTitle(placeMark.toString());
                }
                mFloatingPOIdataOverlay.selectPOIitemBy(mFloatingPOIitem.getId(), false);
            }
        }

    };
    /* MyLocation Listener */
    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

        @Override
        public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {

            if (mMapController != null) {
                mMapController.animateTo(myLocation);
            }

            return true;
        }

        @Override
        public void onLocationUpdateTimeout(NMapLocationManager locationManager) {

            // stop location updating
            //			Runnable runnable = new Runnable() {
            //				public void run() {
            //					stopMyLocation();
            //				}
            //			};
            //			runnable.run();

            Toast.makeText(getContext(), "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {

            Toast.makeText(getContext(), "Your current location is unavailable area.", Toast.LENGTH_LONG).show();

            stopMyLocation();
        }

    };

    private final NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener() {

        @Override
        public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {

            if (errorInfo == null) { // success
                // restore map view state such as map center position and zoom level.
                //restoreInstanceState();
                if( flag == 0 ) startMyLocation();
                flag = 1;
            } else { // fail
                Log.e(LOG_TAG, "onFailedToInitializeWithError: " + errorInfo.toString());

                Toast.makeText(getContext(), errorInfo.toString(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onAnimationStateChange(NMapView mapView, int animType, int animState) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onAnimationStateChange: animType=" + animType + ", animState=" + animState);
            }
        }

        @Override
        public void onMapCenterChange(NMapView mapView, NGeoPoint center) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onMapCenterChange: center=" + center.toString());
            }
        }

        @Override
        public void onZoomLevelChange(NMapView mapView, int level) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onZoomLevelChange: level=" + level);
            }
        }

        @Override
        public void onMapCenterChangeFine(NMapView mapView) {

        }
    };
    private final NMapView.OnMapViewTouchEventListener onMapViewTouchEventListener = new NMapView.OnMapViewTouchEventListener() {

        @Override
        public void onLongPress(NMapView mapView, MotionEvent ev) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLongPressCanceled(NMapView mapView) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSingleTapUp(NMapView mapView, MotionEvent ev) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTouchDown(NMapView mapView, MotionEvent ev) {

        }

        @Override
        public void onScroll(NMapView mapView, MotionEvent e1, MotionEvent e2) {
        }

        @Override
        public void onTouchUp(NMapView mapView, MotionEvent ev) {
            // TODO Auto-generated method stub

        }

    };

    private final NMapView.OnMapViewDelegate onMapViewTouchDelegate = new NMapView.OnMapViewDelegate() {

        @Override
        public boolean isLocationTracking() {
            if (mMapLocationManager != null) {
                if (mMapLocationManager.isMyLocationEnabled()) {
                    return mMapLocationManager.isMyLocationFixed();
                }
            }
            return false;
        }

    };

    private final NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {

        @Override
        public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onCalloutClick: title=" + item.getTitle());
            }
            int markerId = item.getMarkerId();
            String m_title =  item.getTitle();
            String m_address = "";
            String m_call = "";
            String m_point="";
            if ( markerId == NMapPOIflagType.PIN) {
                markerId = 0;
            }
            else if ( markerId == NMapPOIflagType.PIN + 1) {
                markerId = 1;
            }
            else if ( markerId == NMapPOIflagType.PIN + 2) {
                markerId = 2;
            }
            else if ( markerId == NMapPOIflagType.PIN + 3) {
                markerId = 3;
            }
            for(int i = 0; i < A[markerId]; i++) {
                if ( m_title == Ssport1[markerId][i]) {
                    m_address = Address[markerId][i];
                    m_call = Call[markerId][i];
                    m_point = "" + sport[markerId][i][0] + ", " + sport[markerId][i][1];
                }
            }

            Intent intent = new Intent(getActivity(), PopupActivity.class);
            intent.putExtra("title", m_title); // 여기서 상세정보 보내주면 됨
            intent.putExtra("address", m_address);
            intent.putExtra("call" , m_call);
            intent.putExtra("point", m_point);
            startActivityForResult(intent, 1);

            // [[TEMP]] handle a click event of the callout
            //Toast.makeText(getContext(), "여기바꾸면됩니다: " + item.getTitle(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFocusChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
            if (DEBUG) {
                if (item != null) {
                    Log.i(LOG_TAG, "onFocusChanged: " + item.toString());
                } else {
                    Log.i(LOG_TAG, "onFocusChanged: ");
                }
            }
        }
    };

    private final NMapOverlayManager.OnCalloutOverlayListener onCalloutOverlayListener = new NMapOverlayManager.OnCalloutOverlayListener() {

        @Override
        public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay itemOverlay, NMapOverlayItem overlayItem,
                                                         Rect itemBounds) {

            // handle overlapped items
            if (itemOverlay instanceof NMapPOIdataOverlay) {
                NMapPOIdataOverlay poiDataOverlay = (NMapPOIdataOverlay)itemOverlay;

                // check if it is selected by touch event
                if (!poiDataOverlay.isFocusedBySelectItem()) {
                    int countOfOverlappedItems = 1;

                    NMapPOIdata poiData = poiDataOverlay.getPOIdata();
                    for (int i = 0; i < poiData.count(); i++) {
                        NMapPOIitem poiItem = poiData.getPOIitem(i);

                        // skip selected item
                        if (poiItem == overlayItem) {
                            continue;
                        }

                        // check if overlapped or not
                        if (Rect.intersects(poiItem.getBoundsInScreen(), overlayItem.getBoundsInScreen())) {
                            countOfOverlappedItems++;
                        }
                    }

                    if (countOfOverlappedItems > 1) {
                        String text = countOfOverlappedItems + " overlapped items for " + overlayItem.getTitle();
                        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
                        return null;
                    }
                }
            }

            // use custom old callout overlay
            if (overlayItem instanceof NMapPOIitem) {
                NMapPOIitem poiItem = (NMapPOIitem)overlayItem;

                if (poiItem.showRightButton()) {
                    return new NMapCalloutCustomOldOverlay(itemOverlay, overlayItem, itemBounds,
                            mMapViewerResourceProvider);
                }
            }

            // use custom callout overlay
            return new NMapCalloutCustomOverlay(itemOverlay, overlayItem, itemBounds, mMapViewerResourceProvider);

            // set basic callout overlay
            //return new NMapCalloutBasicOverlay(itemOverlay, overlayItem, itemBounds);
        }

    };

    private final NMapOverlayManager.OnCalloutOverlayViewListener onCalloutOverlayViewListener = new NMapOverlayManager.OnCalloutOverlayViewListener() {

        @Override
        public View onCreateCalloutOverlayView(NMapOverlay itemOverlay, NMapOverlayItem overlayItem, Rect itemBounds) {

            if (overlayItem != null) {
                // [TEST] 말풍선 오버레이를 뷰로 설정함
                String title = overlayItem.getTitle();
                if (title != null && title.length() > 5) {
                    return new NMapCalloutCustomOverlayView(getActivity(), itemOverlay, overlayItem, itemBounds);
                }
            }

            // null을 반환하면 말풍선 오버레이를 표시하지 않음
            return null;
        }

    };

    private class MapContainerView extends ViewGroup {

        public MapContainerView(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            final int width = getWidth();
            final int height = getHeight();
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final View view = getChildAt(i);
                final int childWidth = view.getMeasuredWidth();
                final int childHeight = view.getMeasuredHeight();
                final int childLeft = (width - childWidth) / 2;
                final int childTop = (height - childHeight) / 2;
                view.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            }
            if (changed) {
                mOverlayManager.onSizeChanged(width, height);
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int w = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            int h = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            int sizeSpecWidth = widthMeasureSpec;
            int sizeSpecHeight = heightMeasureSpec;

            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final View view = getChildAt(i);

                if (view instanceof NMapView) {
                    if (mapView.isAutoRotateEnabled()) {
                        int diag = (((int)(Math.sqrt(w * w + h * h)) + 1) / 2 * 2);
                        sizeSpecWidth = MeasureSpec.makeMeasureSpec(diag, MeasureSpec.EXACTLY);
                        sizeSpecHeight = sizeSpecWidth;
                    }
                }

                view.measure(sizeSpecWidth, sizeSpecHeight);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
