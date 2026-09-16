import { useState } from 'react';
import axios from 'axios';

function CustomerForm() {
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [accountNumber, setAccountNumber] = useState('');
    const [balance, setBalance] = useState('');
    const [message, setMessage] = useState('');

    const handleCreateCustomer = (e) => {
        e.preventDefault();

        const newCustomer = {
            firstName: firstName,
            lastName: lastName,
            accountNumber: accountNumber,
            balance: parseFloat(balance)
        };

        axios.post('http://localhost:8080/api/customers', newCustomer)
            .then(response => {
                setMessage(`✅ Cliente ${response.data.firstName} creado exitosamente con ID: ${response.data.id}`);
                // Limpiar el formulario
                setFirstName('');
                setLastName('');
                setAccountNumber('');
                setBalance('');
            })
            .catch(error => {
                setMessage(`Error al crear el cliente`);
            });
    };

    return (
        <div>
            <h2>Registrar Nuevo Cliente</h2>
            <form onSubmit={handleCreateCustomer} style={{ display: 'flex', flexDirection: 'column', gap: '15px', marginTop: '20px' }}>
                <div>
                    <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Nombre:</label>
                    <input
                        type="text"
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
                        required
                        style={{ width: '100%', padding: '10px', borderRadius: '5px', border: '1px solid #ccc' }}
                    />
                </div>
                <div>
                    <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Apellido:</label>
                    <input
                        type="text"
                        value={lastName}
                        onChange={(e) => setLastName(e.target.value)}
                        required
                        style={{ width: '100%', padding: '10px', borderRadius: '5px', border: '1px solid #ccc' }}
                    />
                </div>
                <div>
                    <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Número de Cuenta:</label>
                    <input
                        type="text"
                        value={accountNumber}
                        onChange={(e) => setAccountNumber(e.target.value)}
                        required
                        style={{ width: '100%', padding: '10px', borderRadius: '5px', border: '1px solid #ccc' }}
                    />
                </div>
                <div>
                    <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Saldo Inicial:</label>
                    <input
                        type="number"
                        value={balance}
                        onChange={(e) => setBalance(e.target.value)}
                        required
                        min="0"
                        step="0.01"
                        style={{ width: '100%', padding: '10px', borderRadius: '5px', border: '1px solid #ccc' }}
                    />
                </div>

                <button type="submit" style={{
                    padding: '12px',
                    cursor: 'pointer',
                    backgroundColor: '#00563F',
                    color: 'white',
                    border: 'none',
                    borderRadius: '8px',
                    fontSize: '1.1rem',
                    fontWeight: 'bold',
                    marginTop: '10px'
                }}>
                    Crear Cliente
                </button>
            </form>

            {message && <p style={{ marginTop: '20px', fontWeight: 'bold', fontSize: '1.1rem' }}>{message}</p>}
        </div>
    );
}

export default CustomerForm;