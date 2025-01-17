const mongoose = require('mongoose');
//esquema para el modelo de la cita
const citaSchema = new mongoose.Schema({
    nombre: {
        type: String,
        required: [true, 'El nombre es obligatorio'],
        trim: true, // Elimina espacios en blanco antes/después del texto
    },
    fecha: {
        type: Date,
        required: [true, 'La fecha es obligatoria'],
        validate: {
            validator: (value) => !isNaN(value.getTime()), // Valida que sea una fecha válida
            message: 'La fecha debe ser válida',
        },
    },
    hora: {
        type: String,
        required: [true, 'La hora es obligatoria'],
        match: [/^\d{2}:\d{2}$/, 'Formato de hora inválido (HH:mm)'], // Valida formato HH:mm
    },
    descripcion: {
        type: String,
        maxlength: [500, 'La descripción no puede superar los 500 caracteres'],
        default: '',
    },
    usuarioId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Usuario', // Relación con la colección Usuario
        required: [true, 'El usuario asociado es obligatorio'],
    },
    status: { 
        type: String, 
        default: 'Pendiente', 
        enum: ['Pendiente', 'Aceptada', 'Pospuesta', 'Cancelada'] 
    },
}, {
    timestamps: true, // Agrega campos createdAt y updatedAt automáticamente
});

// Índices para búsquedas eficientes
citaSchema.index({ fecha: 1, usuarioId: 1 }); // Optimiza búsquedas por fecha y usuario

// Pre-guardado: Verificar si la cita ya existe
citaSchema.pre('save', async function(next) {
    const cita = this;
    
    // Verificar si ya existe una cita para el mismo usuario en la misma fecha y hora
    const citaExistente = await mongoose.models.Cita.findOne({
        usuarioId: cita.usuarioId,
        fecha: cita.fecha,
        hora: cita.hora
    });

    if (citaExistente) {
        return next(new Error('Ya existe una cita para este usuario en la misma fecha y hora.'));
    }
    next();
});

// Crear el modelo de la cita
const Cita = mongoose.model('Cita', citaSchema);

// Exportar el modelo
module.exports = Cita;
