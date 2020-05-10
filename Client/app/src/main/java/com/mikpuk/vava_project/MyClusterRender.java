package com.mikpuk.vava_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

/**
 * Custom cluster render
 *
 * This class manages custom markers on the map
 *
 */
public class MyClusterRender extends DefaultClusterRenderer<MyMarker> {

    private final IconGenerator iconGenerator;
    private ImageView imageView;
    private final int width;
    private final int height;
    GoogleMap mMap;

    public MyClusterRender(Context context, GoogleMap map, ClusterManager<MyMarker> clusterManager) {

        super(context, map, clusterManager);
        mMap = map;
        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        width = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        height = (int) context.getResources().getDimension(R.dimen.custom_marker_image);

        imageView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        int padding = (int) context.getResources().getDimension(R.dimen.custom_marker_padding);
        imageView.setPadding(padding,padding,padding,padding);
        iconGenerator.setContentView(imageView);

    }

    /**
     * Setting custom image to marker
     * @param item marker
     * @param markerOptions options for marker
     */

    @Override
    protected void onBeforeClusterItemRendered(MyMarker item, MarkerOptions markerOptions) {
        imageView.setImageResource(item.getIconPicture());
        imageView.setBackgroundColor(Color.parseColor("#3b486b"));
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<MyMarker> cluster) {

        return false;
    }
}
