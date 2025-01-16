const mongoose = require('mongoose');

// Esquema para el modelo de la cita
const citaSchema = new mongoose.Schema({
    nombre: {
        type: String,
        required: [true, 'El nombre es obligatorio'],
        trim: true, // Elimina espacios en blanco antes/después del texto
    },
    motivo: {
        type: String,
        required: [true, 'El motivo de la cita es obligatorio'],
        maxlength: [300, 'El motivo no puede superar los 300 caracteres'],
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
        validate: {
            validator: (value) => {
                const minutos = parseInt(value.split(":")[1], 10);
                return minutos === 0 || minutos === 30; // Valida que la hora sea en múltiplos de 30 minutos
            },
            message: 'La hora debe ser un múltiplo de 30 minutos (ej. 9:00, 9:30, etc.)'
        }
    },
    genero: {
        type: String,
        enum: ['Masculino', 'Femenino', 'Otro'],
        required: [true, 'El género es obligatorio'],
    },
    edad: {
        type: Number,
        required: [true, 'La edad es obligatoria'],
        min: [0, 'La edad no puede ser menor a 0'],
    },
    telefono: {
        type: String,
        required: [true, 'El teléfono es obligatorio'],
        match: [/^\d{10}$/, 'El teléfono debe tener 10 dígitos'],
    },
    estadoCivil: {
        type: String,
        enum: ['Soltero', 'Casado', 'Divorciado', 'Viudo'],
        required: [true, 'El estado civil es obligatorio'],
    },
    domicilio: {
        type: String,
        required: [true, 'El domicilio es obligatorio'],
        maxlength: [300, 'El domicilio no puede superar los 300 caracteres'],
    },
    email: {
        type: String,
        required: [true, 'El correo electrónico es obligatorio'],
        match: [/^\S+@\S+\.\S+$/, 'El correo debe tener un formato válido'],
    },
    comentarios: {
        type: String,
        maxlength: [500, 'Los comentarios no pueden superar los 500 caracteres'],
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
