const mongoose = require('mongoose');

const citaSchema = new mongoose.Schema({
    nombre: { type:String, required: true},
    fecha: { type: Date, required: true},
    hora: {type: String, required: true},
    descripcion: { type: String, required: false}
});

module.exports = mongoose.model('Cita', citaSchema);