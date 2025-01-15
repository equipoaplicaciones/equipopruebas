const mongoose = require('mongoose');

const citaSchema = new mongoose.Schema({
    nombre: { type: String, required: [true, 'El nombre es obligatorio'] },
    fecha: { type: Date, required: [true, 'La fecha es obligatoria'] },
    hora: { type: String, required: [true, 'La hora es obligatoria'], match: [/^\d{2}:\d{2}$/, 'Formato de hora inválido (HH:mm)'] },
    descripcion: { type: String, maxlength: [500, 'La descripción no puede superar los 500 caracteres'] },
    usuarioId: { type: mongoose.Schema.Types.ObjectId, ref: 'Usuario', required: true }
});

// Crear el modelo de la cita
const Cita = mongoose.model('Cita', citaSchema);

// Exportar el modelo
module.exports = Cita;
