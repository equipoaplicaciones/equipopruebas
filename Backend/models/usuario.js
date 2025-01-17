const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');

// Definimos el esquema para el usuario
const usuarioSchema = new mongoose.Schema({
    nombre: { type: String, required: true },
    email: {
        type: String,
        required: true,
        unique: true,  // Asegura que el correo sea único
        match: [/^\S+@\S+\.\S+$/, 'Por favor ingresa un correo electrónico válido'], // Validación de formato de correo
    },
    contrasena: { 
        type: String, 
        required: true,
        minlength: [6, 'La contraseña debe tener al menos 6 caracteres'] // Validación de longitud mínima
    }
});
//se encripta la contraseña antes de guardarla
usuarioSchema.pre('save', async function(next) {
    // Verificamos si la contraseña ha sido modificada o es nueva
    if (this.isModified('contrasena')) {
        // Encriptamos la contraseña con bcrypt antes de guardarla
        this.contrasena = await bcrypt.hash(this.contrasena, 10);  // 10 es el valor de "salt rounds"
    }
    next();
});

module.exports = mongoose.model('Usuario', usuarioSchema);
