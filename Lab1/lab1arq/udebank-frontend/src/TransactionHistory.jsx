import { useState } from 'react';
import axios from 'axios';

function TransactionHistory() {
    const [accountNumber, setAccountNumber] = useState('');
    const [history, setHistory] = useState([]);

    const handleSearch = () => {
        axios.get('http://localhost:8080/api/transactions/' + accountNumber)
            .then(response => {
                setHistory(response.data);
            })
            .catch(error => {
                console.error("Error al obtener el historial de transacciones", error);
            });
    };

    return (
        <div>
            <h2>Historial de Transacciones</h2>

            <div style={{ marginBottom: '20px' }}>
                <input
                    type="text"
                    placeholder="Ingrese número de cuenta"
                    value={accountNumber}
                    onChange={(e) => setAccountNumber(e.target.value)}
                />
                <button onClick={handleSearch}>Buscar</button>
            </div>

            {history.length > 0 && (
                <table style={{ width: '100%', textAlign: 'left', borderCollapse: 'collapse' }}>
                <thead>
                <tr style={{ borderBottom: '2px solid #000' }}>
                    <th>Monto</th>
                    <th>Fecha</th>
                    <th>Cuenta de Origen</th>
                    <th>Cuenta de Destino</th>
                </tr>
                </thead>
                    <tbody>
                        {history.map((t, index) => (
                            <tr key={t.id} style={{ borderBottom: '1px solid #eee' }}>
                                <td>{t.amount}</td>
                                <td>{t.timestamp}</td>
                                <td>{t.senderAccountNumber}</td>
                                <td>{t.receiverAccountNumber}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default TransactionHistory;