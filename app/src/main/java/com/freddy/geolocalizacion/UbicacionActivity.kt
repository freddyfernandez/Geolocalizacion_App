package com.freddy.geolocalizacion

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class UbicacionActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedlocationclient:FusedLocationProviderClient
    private  lateinit var locationRequest:LocationRequest
    private var coords=LatLng(-16.39889,-71.535)
    val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=44

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubicacion)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        /*instanciar ubicacion actual*/
        fusedlocationclient= LocationServices.getFusedLocationProviderClient(this)
    }


    /*Metodos creados*/

    /*habilitar ubicacion*/
    private fun isLocEnable():Boolean{
        var locationManager: LocationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)|| locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /*habilitar permisos*/
    private fun isPermissionGiven():Boolean{
        return  ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED


    }

    /*obtener ubicacion actual*/

    private  fun getCurrentLocation(){
        if(isLocEnable()){
           fusedlocationclient.lastLocation
               .addOnSuccessListener { location: Location? ->
                   if (location!=null)
                   {
                       val newCoords=LatLng(location.latitude,location.longitude)

                       coords=newCoords
                       mMap.addMarker(MarkerOptions().position(coords).title("Posicion Actual").snippet("esta ubicado aqui"))
                       mMap.moveCamera(CameraUpdateFactory.newLatLng(coords))
                       mMap.setMinZoomPreference(13.0f)
                       mMap.setMaxZoomPreference(16.0f)

                   }
                   else
                   {
                       getNewLocationUser()
                   }
               }
               .addOnFailureListener{error: Exception?-> Log.d("ERROR", error?.message)}
        }
    }

    private  fun getNewLocationUser(){
        locationRequest=LocationRequest()
        locationRequest.interval=0
        locationRequest.priority=LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.fastestInterval=0
        locationRequest.numUpdates=2

        fusedlocationclient.requestLocationUpdates(locationRequest,locationCallBack, Looper.myLooper())
    }

    private val  locationCallBack= object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation: Location=p0.lastLocation
            val newCoords=LatLng(lastLocation.latitude,lastLocation.longitude)

            coords=newCoords
            mMap.addMarker(MarkerOptions().position(coords).title("Posicion Actual").snippet("esta ubicado aqui"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(coords))
            mMap.setMinZoomPreference(13.0f)
            mMap.setMaxZoomPreference(16.0f)
        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {

        //editar campos de ubicacion

        /*
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coords))
        */

        mMap = googleMap
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coords))
        mMap.setMinZoomPreference(13.0f)
        mMap.setMaxZoomPreference(16.0f)

        if(isPermissionGiven()){
            getCurrentLocation()

        }
        else
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION),MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION->{
                if ((grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED))
                {
                    getCurrentLocation()
                }
                else
                {
                    Log.d("ERROR", "EL USUARIO NO PERMITIO ACCESSO A LA APLICACION")
                }
                return


            }
        }
    }

}
