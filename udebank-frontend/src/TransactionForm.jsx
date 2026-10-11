import { useState } from 'react';
import axios from 'axios';

function TransactionForm() {
    const [senderAccount, setSenderAccount] = useState('');
    const [receiverAccount, setReceiverAccount] = useState('');
    const [amount, setAmount] = useState('');
    const [message, setMessage] = useState('');
    const [timestamp, setTimestamp] = useState('');

    const handleTransfer = (e) => {
        e.preventDefault();
        const transactionData = {
            senderAccountNumber: senderAccount,
            receiverAccountNumber: receiverAccount,
            amount: parseFloat(amount),
            timestamp: new Date().toLocaleString('sv-SE') // YYYY-MM-DD HH:mm:ss
        };

        axios.post('http://localhost:8080/api/transactions', transactionData)
            .then(response => {
                setMessage(`✅ Transferencia exitosa. ID de transacción: ${response.data.id}`);
                // Limpiamos los campos
                setSenderAccount('');
                setReceiverAccount('');
                setAmount('');
            })
            .catch(error => {
                setMessage(`❌ Error: ${error.response?.data || 'No se pudo realizar la transferencia'}`);
            });
    };

    return (
        <div style={{ padding: '20px', border: '1px solid #ccc', borderRadius: '8px', marginTop: '20px' }}>
            <h2>Realizar Transferencia</h2>
            <form onSubmit={handleTransfer} style={{ display: 'flex', flexDirection: 'column', gap: '15px', marginTop: '20px' }}>
                <div>
                    <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Cuenta Origen: </label>
                    <input
                        type="text"
                        value={senderAccount}
                        onChange={(e) => setSenderAccount(e.target.value)}
                        required
                        style={{ width: '90%', padding: '10px', borderRadius: '5px', border: '1px solid #ccc' }}
                    />
                </div>
                <div>
                    <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Cuenta Destino: </label>
                    <input
                        type="text"
                        value={receiverAccount}
                        onChange={(e) => setReceiverAccount(e.target.value)}
                        required
                        style={{ width: '90%', padding: '10px', borderRadius: '5px', border: '1px solid #ccc' }}
                    />
                </div>
                <div>
                    <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Monto: </label>
                    <input
                        type="number"
                        value={amount}
                        onChange={(e) => setAmount(e.target.value)}
                        required
                        style={{ width: '90%', padding: '10px', borderRadius: '5px', border: '1px solid #ccc' }}
                    />
                </div>
                <button type="submit" style={{ padding: '10px', cursor: 'pointer', backgroundColor: '#00563F', color: 'white', border: 'none', borderRadius: '5px' }}>
                    Transferir Dinero
                </button>
            </form>
            {message && <p style={{ marginTop: '15px', fontWeight: 'bold' }}>{message}</p>}
        </div>
    );
}

export default TransactionForm;