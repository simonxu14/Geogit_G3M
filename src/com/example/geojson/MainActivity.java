package com.example.geojson;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.glob3.mobile.generated.AltitudeMode;
import org.glob3.mobile.generated.Angle;
import org.glob3.mobile.generated.Color;
import org.glob3.mobile.generated.ElevationDataProvider;
import org.glob3.mobile.generated.GEO2DLineStringGeometry;
import org.glob3.mobile.generated.GEO2DMultiLineStringGeometry;
import org.glob3.mobile.generated.GEO2DMultiPolygonGeometry;
import org.glob3.mobile.generated.GEO2DPointGeometry;
import org.glob3.mobile.generated.GEO2DPolygonGeometry;
import org.glob3.mobile.generated.GEOFeature;
import org.glob3.mobile.generated.GEOMarkSymbol;
import org.glob3.mobile.generated.GEORasterLineSymbol;
import org.glob3.mobile.generated.GEORasterPolygonSymbol;
import org.glob3.mobile.generated.GEORenderer;
import org.glob3.mobile.generated.GEOSymbol;
import org.glob3.mobile.generated.GEOSymbolizer;
import org.glob3.mobile.generated.Geodetic2D;
import org.glob3.mobile.generated.Geodetic3D;
import org.glob3.mobile.generated.JSONNumber;
import org.glob3.mobile.generated.JSONObject;
import org.glob3.mobile.generated.LayerSet;
import org.glob3.mobile.generated.MapBoxLayer;
import org.glob3.mobile.generated.Mark;
import org.glob3.mobile.generated.MarkTouchListener;
import org.glob3.mobile.generated.Sector;
import org.glob3.mobile.generated.SingleBilElevationDataProvider;
import org.glob3.mobile.generated.TimeInterval;
import org.glob3.mobile.generated.URL;
import org.glob3.mobile.generated.Vector2I;
import org.glob3.mobile.specific.G3MBuilder_Android;
import org.glob3.mobile.specific.G3MWidget_Android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.RelativeLayout;


@SuppressLint("SdCardPath")
public class MainActivity
         extends
            Activity {

   private G3MWidget_Android _g3mWidget;
   private RelativeLayout    _placeHolder;
   
   private GEORenderer       _vectorialRenderer;//Renderer


@Override
   protected void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      final float _VerticalExaggeration = 2f;
      final LayerSet layerSet = new LayerSet();

      
      final MapBoxLayer mboxTerrainLayer = new MapBoxLayer("examples.map-qogxobv1", TimeInterval.fromDays(30), true, 5);
      layerSet.addLayer(mboxTerrainLayer);


      final G3MBuilder_Android builder = new G3MBuilder_Android(this);
      builder.getPlanetRendererBuilder().setLayerSet(layerSet);


      builder.setBackgroundColor(Color.fromRGBA255(185, 221, 209, 255).muchDarker());


      final Geodetic2D lower = new Geodetic2D(
               Angle.fromDegrees(43.69200778158779),
               Angle.fromDegrees(7.36351850323685));
      final Geodetic2D upper = new Geodetic2D(
               Angle.fromDegrees(43.7885865186124),
               Angle.fromDegrees(7.48617349925817));

      final Sector demSector = new Sector(lower, upper);
      
      

      builder.getPlanetRendererBuilder().setVerticalExaggeration(_VerticalExaggeration);

      
      _vectorialRenderer = builder.createGEORenderer(Symbolizer);
      
      _vectorialRenderer.loadJSON(new URL("file:///buildings.geojson"));
      _vectorialRenderer.loadJSON(new URL("file:///roads.geojson"));
      _vectorialRenderer.loadJSON(new URL("file:///restaurants.geojson"));
      
//      SDPath sdpath = new SDPath ();
//  	  String path = sdpath.getSDPath();
//      _vectorialRenderer.loadJSON(new URL("file:///" + path +"/geojson/buildings.geojson"));
//      _vectorialRenderer.loadJSON(new URL("file:///" + path +"/geojson/roads.geojson"));
//      _vectorialRenderer.loadJSON(new URL("file:///" + path +"/geojson/restaurants.geojson"));
      


      builder.setShownSector(demSector.shrinkedByPercent(0.1f));


      _g3mWidget = builder.createWidget();

      _placeHolder = (RelativeLayout) findViewById(R.id.g3mWidgetHolder_main);
      _placeHolder.addView(_g3mWidget);


   }


   @Override
   public void onBackPressed() {
      System.exit(0);
   }

   GEOSymbolizer Symbolizer = new GEOSymbolizer() {

                               @Override
                               public ArrayList<GEOSymbol> createSymbols(final GEO2DMultiPolygonGeometry geometry) {
                                  return null;
                               }

//							   PolygonGemetry
                               @Override
                               public ArrayList<GEOSymbol> createSymbols(final GEO2DPolygonGeometry geometry) {
                                  final ArrayList<GEOSymbol> symbols = new ArrayList<GEOSymbol>();
                                  final JSONObject properties = geometry.getFeature().getProperties();
                                  final String name = properties.getAsString("name","");
                                  final JSONNumber osm_id = properties.getAsNumber("osm_id");
                                  final Double id = osm_id.value();
                                  
                                  final MarkTouchListener markListener = new MarkTouchListener() {
                                      @Override
                                      public boolean touchedMark(final Mark mark) {
                                         runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                               new AlertDialog.Builder(MainActivity.this)
                                               .setTitle("Buildings Name")
                                               .setMessage(name)
                                               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                  @Override
                                                  public void onClick(final DialogInterface dialog,
                                                                      final int which) {
                                                  }
                                               })
                                               .setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                                                  @Override
                                                  public void onClick(final DialogInterface dialog,
                                                                      final int which) {
                                                  }
                                               })
                                               .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                  @Override
                                                  public void onClick(final DialogInterface dialog,
                                                                      final int which) {
                                                	 SDPath sdpath = new SDPath ();
                                                 	 String path = sdpath.getSDPath() + "/geojson/buildings.geojson";
                      								 String geojson = WriteOrReadGeoJson.deleteGeoJson(path,id);
                      								 System.out.println(geojson); 	 
                      								 
                      								 Intent intent = new Intent();  
                     				                 intent.setClass(MainActivity.this, MainActivity.class);  
                     				                 startActivity(intent);  
                     				                 finish();
                      								 
                                                 	 
                                                  }
                                               })
                                               .show();
                                            }
                                         });

                                         return true;
                                      }
                                   };
                                  
                                  
                                   final Mark mark = new Mark(
                                           new URL("file:///building.png"),
                                           new Geodetic3D(geometry.getCoordinates().get(0), 0),
                                           AltitudeMode.RELATIVE_TO_GROUND,
                                           5000,
                                           null,
                                           false,
                                           markListener,
                                           true);
                                      
                                    symbols.add(new GEOMarkSymbol(mark));
                                  
                                  
                                  
                                  
                                  symbols.add(new GEORasterPolygonSymbol(geometry.getPolygonData(),
                                           Symbology.createPolygonLineRasterStyle(geometry),
                                           Symbology.createPolygonSurfaceRasterStyle(geometry)));

                                  return symbols;
                               }


                               @Override
                               public ArrayList<GEOSymbol> createSymbols(final GEO2DMultiLineStringGeometry geometry) {
                                  return null;
                               }

//							   LineStringGeometry
                               @Override
                               public ArrayList<GEOSymbol> createSymbols(final GEO2DLineStringGeometry geometry) {
                                  final ArrayList<GEOSymbol> symbols = new ArrayList<GEOSymbol>();
                                  final JSONObject properties = geometry.getFeature().getProperties();
                                  final String name = properties.getAsString("name","");
                                  final JSONNumber osm_id = properties.getAsNumber("osm_id");
                                  final Double id = osm_id.value();
                                  
                                  
                                  
                                  final MarkTouchListener markListener = new MarkTouchListener() {
                                      @Override
                                      public boolean touchedMark(final Mark mark) {
                                         runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                               new AlertDialog.Builder(MainActivity.this)
                                               .setTitle("Roads Name")
                                               .setMessage(name)
                                               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                  @Override
                                                  public void onClick(final DialogInterface dialog,
                                                                      final int which) {
                                                  }
                                               })
                                               .setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                                                  @Override
                                                  public void onClick(final DialogInterface dialog,
                                                                      final int which) {
                                                  }
                                               })
                                               .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                  @Override
                                                  public void onClick(final DialogInterface dialog,
                                                                      final int which) {
                                                	 SDPath sdpath = new SDPath ();
                                                  	 String path = sdpath.getSDPath() + "/geojson/roads.geojson";
                      								 String geojson = WriteOrReadGeoJson.deleteGeoJson(path,id);
                      								 System.out.println(geojson); 	 
                      								 
                      								 Intent intent = new Intent();  
                     				                 intent.setClass(MainActivity.this, MainActivity.class);  
                     				                 startActivity(intent);  
                     				                 finish();
                      								 
                                                 	 
                                                  }
                                               })
                                               .show();
                                            }
                                         });

                                         return true;
                                      }
                                   };
                                  
                                  
                                   final Mark mark = new Mark(
                                           new URL("file:///road.png"),
                                           new Geodetic3D(geometry.getCoordinates().get(0), 0),
                                           AltitudeMode.RELATIVE_TO_GROUND,
                                           5000,
                                           null,
                                           false,
                                           markListener,
                                           true);
                                      
                                    symbols.add(new GEOMarkSymbol(mark));
                                  
                                  
                                  
                                  symbols.add(new GEORasterLineSymbol(geometry.getCoordinates(),
                                           Symbology.createLineRasterStyle(geometry)));
                                  
                                  return symbols;
                               }

//                             PointGeometry
							   @Override
                               public ArrayList<GEOSymbol> createSymbols(final GEO2DPointGeometry geometry) {

                                  final ArrayList<GEOSymbol> result = new ArrayList<GEOSymbol>();
                                  final JSONObject properties = geometry.getFeature().getProperties();
//                                final GEOFeature feature = geometry.getFeature();
                                  final String name = properties.getAsString("name","");
                                  final JSONNumber osm_id = properties.getAsNumber("osm_id");
                                  final Double id = osm_id.value();
                               
                                  final MarkTouchListener markListener = new MarkTouchListener() {
                                     @Override
                                     public boolean touchedMark(final Mark mark) {
                                        runOnUiThread(new Runnable() {
                                           @Override
                                           public void run() {
                                              new AlertDialog.Builder(MainActivity.this)
                                              .setTitle("Restaurant Name")
                                              .setMessage(name)
                                              .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                 @Override
                                                 public void onClick(final DialogInterface dialog,
                                                                     final int which) {
                                                 }
                                              })
                                              .setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                                                 @Override
                                                 public void onClick(final DialogInterface dialog,
                                                                     final int which) {
                                                 }
                                              })
                                              .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                 @Override
                                                 public void onClick(final DialogInterface dialog,
                                                                     final int which) {
                                                	 SDPath sdpath = new SDPath ();
                                                 	 String path = sdpath.getSDPath() + "/geojson/restaurants.geojson";
                     								 String geojson = WriteOrReadGeoJson.deleteGeoJson(path,id);
                     								 System.out.println(geojson); 	 
                     								 
                     								 Intent intent = new Intent();  
                    				                 intent.setClass(MainActivity.this, MainActivity.class);  
                    				                 startActivity(intent);  
                    				                 finish();
                     								 
                                                	 
                                                 }
                                              })
                                              .show();
                                           }
                                        });

                                        return true;
                                     }
                                  };
                                  
                                  
                                  final Mark mark = new Mark(
                                          new URL("file:///restaurant.png"),
                                          new Geodetic3D(geometry.getPosition(), 0),
                                          AltitudeMode.RELATIVE_TO_GROUND,
                                          5000,
                                          null,
                                          false,
                                          markListener,
                                          true);
                                     
                                   result.add(new GEOMarkSymbol(mark));
                                   return result;                                 
                                      
                               }
                            };
                                                   
//                            public String getSDPath(){ 
//                            	String sdDir = null; 
//                            	boolean sdCardExist = Environment.getExternalStorageState() 
//                            	.equals(android.os.Environment.MEDIA_MOUNTED);
//                            	if (sdCardExist) 
//                            	{ 
//                            	sdDir = Environment.getExternalStorageDirectory().getAbsolutePath();
//                            	} 
//                            	return sdDir; 
//
//                            	} 

}
