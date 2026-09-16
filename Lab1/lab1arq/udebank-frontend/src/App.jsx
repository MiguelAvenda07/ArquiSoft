import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import CustomerList from './CustomerList.jsx';
import TransactionForm from './TransactionForm.jsx';
import TransactionHistory from './TransactionHistory.jsx';
import Customer from './CustomerForm.jsx';
import miImagen from './assets/udea.png';

function App() {
    return (
        <BrowserRouter>
            <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>

                <header style={{
                    backgroundColor: '#00563f',
                    color: 'white',
                    padding: '25px 40px',
                    boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
                    zIndex: 10
                }}>
                    <h1 style={{ margin: 0, fontSize: '2.5rem', fontWeight: '800', letterSpacing: '1px' }}>
                        <Link to="/" style={{ color: 'white', textDecoration: 'none' }}>UdeBank</Link>
                    </h1>
                </header>

                <main style={{ flex: 1, padding: '40px', display: 'flex', justifyContent: 'center' }}>

                    <Routes>
                        <Route path="/" element={
                            <div style={{ display: 'flex', width: '100%', maxWidth: '1200px', gap: '40px' }}>
                                <div style={{
                                    flex: 1, backgroundColor: 'white', borderRadius: '16px',
                                    boxShadow: '0 10px 15px rgba(0,0,0,0.05)', display: 'flex',
                                    alignItems: 'center', justifyContent: 'center', padding: '40px',
                                    border: '10px solid #00563f'
                                }}>
                                    <img src={miImagen} alt="UdeA" style={{ maxWidth: '70%', height: 'auto' }} />
                                </div>

                                <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
                                    <h2 style={{ marginTop: 0, fontSize: '2rem', color: '#1a1a1a', marginBottom: '30px' }}>
                                        El banco oficial de la UdeA
                                    </h2>
                                    <nav>
                                        <Link to="/registrar" className="menu-link">Crear Cliente</Link>
                                        <Link to="/clientes" className="menu-link">Lista de Clientes</Link>
                                        <Link to="/transferir" className="menu-link">Realizar Transferencia</Link>
                                        <Link to="/historial" className="menu-link">Historial de Transacciones</Link>
                                    </nav>
                                </div>
                            </div>
                        } />

                        {/* VISTAS CON BOTÓN DE REGRESO */}
                        <Route path="/registrar" element={
                            <div style={{ width: '100%', maxWidth: '900px' }}>
                                <Link to="/" className="back-button">← Volver al Menú</Link>
                                <div style={{ backgroundColor: 'white', padding: '40px', borderRadius: '16px', boxShadow: '0 4px 15px rgba(0,0,0,0.05)' }}>
                                    <Customer />
                                </div>
                            </div>
                        } />

                        <Route path="/clientes" element={
                            <div style={{ width: '100%', maxWidth: '900px' }}>
                                <Link to="/" className="back-button">← Volver al Menú</Link>
                                <div style={{ backgroundColor: 'white', padding: '40px', borderRadius: '16px', boxShadow: '0 4px 15px rgba(0,0,0,0.05)' }}>
                                    <CustomerList />
                                </div>
                            </div>
                        } />

                        <Route path="/transferir" element={
                            <div style={{ width: '100%', maxWidth: '600px' }}>
                                <Link to="/" className="back-button">← Volver al Menú</Link>
                                <div style={{ backgroundColor: 'white', padding: '40px', borderRadius: '16px', boxShadow: '0 4px 15px rgba(0,0,0,0.05)' }}>
                                    <TransactionForm />
                                </div>
                            </div>
                        } />

                        <Route path="/historial" element={
                            <div style={{ width: '100%', maxWidth: '900px' }}>
                                <Link to="/" className="back-button">← Volver al Menú</Link>
                                <div style={{ backgroundColor: 'white', padding: '40px', borderRadius: '16px', boxShadow: '0 4px 15px rgba(0,0,0,0.05)' }}>
                                    <TransactionHistory />
                                </div>
                            </div>
                        } />

                    </Routes>
                </main>
            </div>
        </BrowserRouter>
    );
}

export default App;