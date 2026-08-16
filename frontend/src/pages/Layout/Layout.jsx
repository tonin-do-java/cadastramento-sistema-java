import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

const Layout = ({ children }) => {
  const location = useLocation();
  const navigate = useNavigate();
  
  const [nomeUsuario, setNomeUsuario] = useState('');
  const [emailUsuario, setEmailUsuario] = useState('');
  const [menuAberto, setMenuAberto] = useState(false); // Estado para o dropdown do usuário
  
  // Estado para o dropdown do Estoque (inicia aberto se estiver em uma rota de estoque)
  const [estoqueAberto, setEstoqueAberto] = useState(
    location.pathname === '/produtos' || 
    location.pathname === '/categorias' || 
    location.pathname === '/movimentacoes'
  );

  useEffect(() => {
    const nomeSalvo = localStorage.getItem('nomeUsuario'); 
    const emailSalvo = localStorage.getItem('emailUsuario'); 
    
    if (nomeSalvo) {
      setNomeUsuario(nomeSalvo);
    } else {
      setNomeUsuario('Usuário');
    }

    if (emailSalvo) {
      setEmailUsuario(emailSalvo);
    } else {
      setEmailUsuario('usuario@email.com'); 
    }
  }, []);

  const isActive = (path) => location.pathname === path;

  const styles = {
    navItem: { padding: '12px 20px', cursor: 'pointer', fontSize: '15px', transition: '0.2s', listStyle: 'none' },
    navItemActive: { padding: '12px 20px', cursor: 'pointer', fontSize: '15px', backgroundColor: '#34495e', borderLeft: '4px solid #3498db', fontWeight: 'bold' },
    subNavItem: { padding: '8px 10px', cursor: 'pointer', fontSize: '14px', color: '#bdc3c7' },
    subNavItemActive: { padding: '8px 10px', cursor: 'pointer', fontSize: '14px', color: '#3498db', fontWeight: 'bold' },
    dropdownItem: { padding: '10px 15px', cursor: 'pointer', fontSize: '14px', color: '#2c3e50', display: 'flex', alignItems: 'center', gap: '10px' }
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate('/');
  };

  return (
    <div style={{ display: 'flex', height: '100vh', backgroundColor: '#f4f6f8', fontFamily: 'sans-serif' }}>
      
      {/* ===== BARRA LATERAL (SIDEBAR) ===== */}
      <div style={{ width: '260px', backgroundColor: '#2c3e50', color: '#ecf0f1', display: 'flex', flexDirection: 'column' }}>
        <div style={{ padding: '20px', borderBottom: '1px solid #34495e', textAlign: 'center' }}>
          <h2 style={{ margin: 0, fontSize: '20px' }}>☕ Café Caseiro ERP</h2>
        </div>
        
        <nav style={{ flex: 1, padding: '20px 0' }}>
          <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
            <li style={isActive('/dashboard') ? styles.navItemActive : styles.navItem} onClick={() => navigate('/dashboard')}>🏠 Dashboard</li>
            
            {/* Item Estoque com Dropdown */}
            <li 
              style={{ ...styles.navItem, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }} 
              onClick={() => setEstoqueAberto(!estoqueAberto)}
            >
              <span>📦 Estoque</span>
              <span style={{ fontSize: '12px', transform: estoqueAberto ? 'rotate(180deg)' : 'rotate(0deg)', transition: '0.3s' }}>
                ▼
              </span>
            </li>
            
            {/* Renderização Condicional dos Sub-itens */}
            {estoqueAberto && (
              <ul style={{ listStyle: 'none', paddingLeft: '40px', margin: '5px 0 15px 0' }}>
                <li style={isActive('/produtos') ? styles.subNavItemActive : styles.subNavItem} onClick={() => navigate('/produtos')}>📦 Produtos</li>
                <li style={isActive('/categorias') ? styles.subNavItemActive : styles.subNavItem} onClick={() => navigate('/categorias')}>🏷 Categorias</li>
                <li style={isActive('/movimentacoes') ? styles.subNavItemActive : styles.subNavItem} onClick={() => navigate('/movimentacoes')}>📊 Moviment.</li>
              </ul>
            )}

            <li style={isActive('/usuario') ? styles.navItemActive : styles.navItem} onClick={() => navigate('/usuario')}>👥 Usuários</li>
            <li style={styles.navItem}>📈 Relatórios</li>
            <li style={styles.navItem}>⚙ Config.</li>
          </ul>
        </nav>

      </div>

      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', overflowY: 'auto' }}>
        
        {/* ===== CABEÇALHO (HEADER) ===== */}
        <header style={{ backgroundColor: '#fff', padding: '15px 30px', display: 'flex', justifyContent: 'flex-end', alignItems: 'center', boxShadow: '0 2px 4px rgba(0,0,0,0.05)' }}>
          <div style={{ marginRight: '30px', cursor: 'pointer', fontSize: '18px' }}>🔔 Notificações</div>
          
          <div style={{ position: 'relative' }}>
            <div 
              onClick={() => setMenuAberto(!menuAberto)} 
              style={{ cursor: 'pointer', fontWeight: 'bold', display: 'flex', alignItems: 'center', userSelect: 'none' }}
            >
              👤 {nomeUsuario} <span style={{ fontSize: '12px', marginLeft: '5px', transform: menuAberto ? 'rotate(180deg)' : 'rotate(0deg)', transition: '0.3s' }}>▼</span>
            </div>

            {menuAberto && (
              <div style={{
                position: 'absolute', top: '100%', right: 0, marginTop: '15px', width: '240px',
                backgroundColor: '#fff', borderRadius: '8px', boxShadow: '0 4px 15px rgba(0,0,0,0.1)',
                border: '1px solid #eee', zIndex: 1000, overflow: 'hidden'
              }}>
                <div style={{ padding: '15px', backgroundColor: '#f8f9fa', borderBottom: '1px solid #eee' }}>
                  <div style={{ fontWeight: 'bold', color: '#2c3e50', fontSize: '15px' }}>👤 {nomeUsuario}</div>
                  <div style={{ color: '#7f8c8d', fontSize: '13px', marginTop: '3px', paddingLeft: '22px' }}>{emailUsuario}</div>
                </div>

                <div style={{ padding: '5px 0' }}>
                  <div style={styles.dropdownItem} onClick={() => {setMenuAberto(false); navigate('/perfil');}}>👤 Meu Perfil</div>
                  <div style={styles.dropdownItem} onClick={() => {setMenuAberto(false); navigate('/alteraSenha')}}>🔐 Alterar Senha</div>
                  <div style={styles.dropdownItem} onClick={() => {setMenuAberto(false); navigate('/preferencias');}}>🎨 Preferências</div>
                  <div style={styles.dropdownItem} onClick={() => setMenuAberto(false)}>❓ Ajuda</div>
                </div>

                <div style={{ borderTop: '1px solid #eee', padding: '5px 0' }}>
                  <div style={{ ...styles.dropdownItem, color: '#e74c3c', fontWeight: 'bold' }} onClick={handleLogout}>🚪 Sair</div>
                </div>
              </div>
            )}
          </div>
        </header>

        <main style={{ padding: '30px', flex: 1, overflowY: 'auto' }}>
          {children}
        </main>
      </div>
    </div>
  );
};

export default Layout;