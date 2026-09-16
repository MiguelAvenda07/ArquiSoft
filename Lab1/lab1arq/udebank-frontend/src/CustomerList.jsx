import { useState, useEffect } from 'react';
import axios from 'axios';

function CustomerList() {
    const [customers, setCustomers] = useState([]);

    useEffect(() => {
        axios.get('http://localhost:8080/api/customers')
            .then(response => {
                setCustomers(response.data);
            })
            .catch(error => {
                console.error("Error al obtener los clientes", error);
            });
    }, []);

    return (
        <div style={{ padding: '20px', border: '1px solid #ccc', borderRadius: '8px' }}>
            <h2>Directorio de Clientes</h2>
            <table style={{ width: '100%', textAlign: 'left', borderCollapse: 'collapse' }}>
                <thead>
                <tr style={{ borderBottom: '2px solid #000' }}>
                    <th>Nombre</th>
                    <th>Apellido</th>
                    <th>No. Cuenta</th>
                    <th>Saldo</th>
                </tr>
                </thead>
                <tbody>
                {customers.map(c => (
                    <tr key={c.id} style={{ borderBottom: '1px solid #eee' }}>
                        <td>{c.firstName}</td>
                        <td>{c.lastName}</td>
                        <td>{c.accountNumber}</td>
                        <td>${c.balance}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default CustomerList;