const express = require('express');
const mongoose = require('mongoose');
require('dotenv').config();
const rutas = require('./routes/rutas');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 5001;

// Middleware para parsear JSON
app.use(express.json());

// Habilitar CORS para permitir solicitudes desde cualquier origen
app.use(cors());

// Conexión a MongoDB Atlas
mongoose.connect(process.env.MONGO_URI || 'mongodb+srv://qwerty:qwerty123@kevin.joqkehr.mongodb.net/dentista?retryWrites=true&w=majority&appName=Kevin', {
    useNewUrlParser: true,
    useUnifiedTopology: true
})
.then(() => {
    console.log('Conectado a MongoDB Atlas');
})
.catch((error) => {
    console.error('Error al conectar a MongoDB Atlas:', error);
});

// Ruta de prueba para verificar el servidor
app.get('/', (req, res) => {
    res.send('¡Servidor Conectado a MongoDB Atlas!');
});

// Rutas principales
app.use(rutas);

// Iniciar el servidor
app.listen(PORT, () => {
    console.log(`Servidor corriendo en http://localhost:${PORT}`);
});
