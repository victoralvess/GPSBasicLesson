package br.com.victoralvess.gpsbasiclesson;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
/*
** 'extends AppCompatActivity' (significa que minha classe 'MainActivity') é uma Activity que pode ter recursos adaptados
** às versões antigas do android (https://cursos.alura.com.br/forum/topico-activity-e-appcompatactivity-20071)
** 'implements OnMapReadyCallback' (implements é uma palavra reservada do JAVA que serve para implementar uma interface)
** interface é uma classe abstrata composta somente por métodos abstratos.
*/
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback { 

    private EditText edtLatitude, edtLongitude; 
    private Button btnObterLocalizacao;
    private GoogleMap mMap; //Objeto para manipular o mapa
    private Marker me; //Marcador para a posição do usuário
    private final LatLng DEFAULT_POSITION = new LatLng(-23.533773, -46.625290); //Constante com as coordenadas de São Paulo 
    private CameraPosition cameraPosition;//Objeto para manipular a câmera

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupElements();
    }

    public void setupElements() {
    	/*
    	** Nas próximas duas linhas, eu pegarei o fragment (assim como o findViewById, mas aqui é findFragmentById)
    	** e como a minha classe implementa OnMapReadyCallback posso colocar 'this' como parâmetro de getMapAsync()
    	** getMapAsync() serve para permitir o uso do método onMapReady().
    	*/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        edtLatitude = (EditText) findViewById(R.id.edtLatitude);
        edtLongitude = (EditText) findViewById(R.id.edtLongitude);

        edtLatitude.setText("" + DEFAULT_POSITION.latitude);
        edtLongitude.setText("" + DEFAULT_POSITION.longitude);

        btnObterLocalizacao = (Button) findViewById(R.id.btnObterLocalizacao);
        btnObterLocalizacao.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                startGPS();
            }
        });
    }
    /*Esse é o código do Jefferson
    ** Esse método define que as coordenadas do usuário serão obtidas pelo GPS (pq, podem obtidas ser pela internet)
    ** O método do listener 'onLocationChanged()' é chamado conforme seu nome diz. Ou seja, se o usuário se mover o método updateView() é chamado
    */
    public void startGPS() {
        LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener lListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                updateView(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        /*Se eu não me engano esse if é util apenas nas novas versões do Android, que pedem permissão em tempo de execução*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lListener);
    }

    public void updateView(Location location) {

    	//Pegando as coordenadas do usuário e exibindo nos EditTexts
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();

        edtLatitude.setText(latitude.toString());
        edtLongitude.setText(longitude.toString());

        /* Método que eu criei para colocar um marcador nas coordenadas do usuário
        ** O 2º parâmetro é o título do marcador (exibido quando o usuário dá um click nele)
        */
        setPosicaoNoMapa(new LatLng(latitude, longitude), getResources().getString(R.string.voceEstaAqui));
    }

    private void setPosicaoNoMapa(LatLng posicaoAtualUsuario, String tituloMarcador) {
        me.setPosition(posicaoAtualUsuario); //Coloca o marcador na posição do usuário
        me.setTitle(tituloMarcador); //Coloca um título que no caso é "Você está aqui"
        // Criando e posicionado a câmera
        cameraPosition = new CameraPosition.Builder()
                                           .target(posicaoAtualUsuario) //Para o local que a câmera apontará
                                           .tilt(85) //inclinação
                                           .zoom(25) //zoom
                                           .bearing(0) // orientação ao oeste
                                           .build(); // encapsulando tudo isso
		//Aqui estou adicionando a câmera criada no mapa                                           
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) { //Esse método vem da classe OnMapReadyCallback
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); //Tipo do mapa (normal, terreno, etc.)
        mMap.setBuildingsEnabled(false); //Consturções 3D (desabilitadas)
        // Customise the styling of the base map using a JSON object defined
        // in a string resource file. First create a MapStyleOptions object
        // from the JSON styles string, then pass this to the setMapStyle
        // method of the GoogleMap object.
        mMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.map_style_json))); //Estilizando o mapa (tem como mudar as cores, neste caso estou usando estilo pokémon go!)
        // Add a marker in default position and move the camera
        me = mMap.addMarker(new MarkerOptions()
                .position(DEFAULT_POSITION)
                .title("São Paulo"));
        //Definindo o icone do marcador (deixei padrão, porém mudei a cor)
        me.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        //Movimentando a câmera para a posição padrão
        mMap.moveCamera(CameraUpdateFactory.newLatLng(DEFAULT_POSITION));

    }
}
