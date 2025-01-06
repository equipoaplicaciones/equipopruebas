const express = require('express');
const mongoose = require('mongoose');
require('dotenv').config();
const rutas = require('./routes/rutas');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 5001;

app.use(express.json());
app.use(cors());

// Conexión a MongoDB Atlas
mongoose.connect('mongodb+srv://kevinleyva:tescha@kevin.joqkehr.mongodb.net/miBaseDeDatos', {
    useNewUrlParser: true,
    useUnifiedTopology: true
})
.then(() => {
    console.log('Conectado a MongoDB Atlas');
})
.catch((error) => {
    console.error('Error al conectar a MongoDB Atlas:', error);
});

// Ruta de prueba para ver si el servidor está funcionando
app.get('/', (req, res) => {
    res.send('¡Servidor Conectado a MongoDB Atlas!');
});

app.use(rutas);

app.listen(PORT, () => {
    console.log(`Servidor corriendo en http://localhost:${PORT}`);
});
