package com.example.applicationinkotlin

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applicationinkotlin.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), SensorEventListener {

    val listUser: List<InfoUser> = listOf(
        InfoUser("User1", "apellido1", 44, "https://cursokotlin.com/wp-content/uploads/2017/07/spiderman.jpg"),
        InfoUser("User2", "apellido2", 4, "https://cursokotlin.com/wp-content/uploads/2017/07/daredevil.jpg"),
        InfoUser("User3", "apellido3", 26, "https://cursokotlin.com/wp-content/uploads/2017/07/logan.jpg"),
    )
    lateinit var etDatepicker: EditText
    lateinit var etTimepicker: EditText
    lateinit var square: TextView
    lateinit var sensorManager : SensorManager
    private lateinit var binding : ActivityMainBinding
    val REQUEST_CODE_TAKE_PHTO = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val button = findViewById<Button>(R.id.button)
        etDatepicker = findViewById(R.id.et_picker)
        etTimepicker = findViewById(R.id.et_hour)
        square = findViewById(R.id.square)

        binding.button.setOnClickListener { checkPermissions() }
        val recycler = findViewById<RecyclerView>(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = Adapter(listUser)

        etDatepicker.setOnClickListener { showDatePickerDialog() }
        etTimepicker.setOnClickListener { showTimePickerDialog() }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        var listSensor = sensorManager.getSensorList(Sensor.TYPE_ALL)

        for (sensor: Sensor in listSensor){
            println("El nombre del sensor es: " + sensor.name)
        }

        setUpSensorStuff()

    }

    private fun setUpSensorStuff() {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            val sides = event.values[0]
            val upDown = event.values[1]

            // TODO: apply se usa como un bloque en el cual queres hacer muchos cambios sobre un mismo objeto.
            //TODO: entonces, a diferencia de let u also. apply se usa para hacer muchas modificaciones sobre el mismo objeto.
            square.apply {
                rotationX = upDown * 3f
                rotationY = sides * 3f
                rotation = -sides
                translationX = sides * -10
                translationY = upDown * 10
            }

            val color = if (upDown.toInt() == 0 && sides.toInt() == 0 ) Color.GREEN else Color.RED
            square.setBackgroundColor(color)

            square.text = "arriba/abajo ${upDown.toInt()}\nizquierda/derecha ${sides.toInt()}"

        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    // ESTO ES PARA ASEGURARSE DE QUE NO HAYA PERDIDA DE MEMORIA O SE USE MEMORIA INNECESARIA CUANDO SE CIERRE LA APP
    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

    private fun showTimePickerDialog() {
        val timePicker = TimePickerFragment {onTimeSelected(it)}
        timePicker.show(supportFragmentManager, "timePicker")
    }

    private fun onTimeSelected(time: String) {
        etTimepicker.setText(time)
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment {dayOfMonth, month, year -> onDateSelected(dayOfMonth, month, year) }
        datePicker.show(supportFragmentManager, "datePicker")
    }

    private fun onDateSelected(day: Int, month: Int, year: Int) {
        etDatepicker.setText(String.format("%02d/%02d/%04d", day, month + 1, year))
    }

    private fun checkPermissions() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            // PERMISO NO ACEPTADO  POR EL USUARIO
            requestCameraPermission()
        } else{
            launchCamera()
        }
    }

    // Si es la primera vez que lo usa y el permiso esta denegado entonces le pedimos el permiso
    private fun requestCameraPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)){
            // EL PERMISO YA HA SIDO RECHAZADO POR EL USUARIO
            Toast.makeText(this, "PERMISOS RECHAZADOS", Toast.LENGTH_SHORT).show()
        } else{
            // PEDIR PERMISO
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 1) //es el codigo del permiso para distinguirlo de los demas
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if( requestCode == 1){ //tenemos nuestros permisos
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                launchCamera()
            }else{
                Toast.makeText(this, "PERMISOS RECHAZADOS POR PRIMERA VEZ", Toast.LENGTH_SHORT).show()
            }
        }
    }

//    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            // There are no request codes
//            val data: Intent? = result.data
//            launchCamera()
//        }
//    }
//
    private fun launchCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePickture ->
            takePickture.resolveActivity(packageManager)?.also {
                startActivityForResult(takePickture, REQUEST_CODE_TAKE_PHTO)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // TODO: LET te permite armar un bloque de codigo respecto de una variable que puede ser nula. porque en caso de que sea nula
        // TODO: el operador if no podria usarse por elmotivo de que justo cuando se llama a esta variable otro hilo la paso a un valor no nulo entonces ya no serviria la variable.
        // TODO: porque kotlin guarda y trabaja los valores nulos.
        if (requestCode == REQUEST_CODE_TAKE_PHTO && resultCode == RESULT_OK) {
            data?.extras?.let { bundle: Bundle ->
                val image = bundle.get("data") as Bitmap
                binding.img.setImageBitmap(image)
            }
        }

    }

//    private fun launchCamera() {
//        val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        startActivityForResult(i, 0)
////        ocultar()
//        Toast.makeText(this, "ABRIENDO CAMARA", Toast.LENGTH_SHORT).show()
//    }

}