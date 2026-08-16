import React, { useState, useEffect } from 'react';

// 1. MOVIDO PARA FORA: Isso garante que o React não recrie o componente do zero, permitindo a animação
const ToggleSwitch = ({ checked, onChange }) => (
  <div 
    onClick={() => onChange(!checked)}
    style={{
      width: '46px', height: '24px', borderRadius: '12px',
      backgroundColor: checked ? '#2ecc71' : '#bdc3c7',
      position: 'relative', cursor: 'pointer', transition: 'background-color 0.3s ease'
    }}
  >
    <div style={{
      width: '18px', height: '18px', borderRadius: '50%', backgroundColor: '#fff',
      position: 'absolute', top: '3px', left: checked ? '25px' : '3px',
      transition: 'left 0.3s ease', boxShadow: '0 1px 3px rgba(0,0,0,0.3)'
    }} />
  </div>
);

const PreferenciasManager = () => {
  const [tema, setTema] = useState('sistema');
  const [notifSistema, setNotifSistema] = useState(true);
  const [notifEstoque, setNotifEstoque] = useState(true);
  const [notifValidade, setNotifValidade] = useState(true);
  const [produtosPorPagina, setProdutosPorPagina] = useState('10');
  const [mostrarInativos, setMostrarInativos] = useState(false);
  const [menuLateral, setMenuLateral] = useState('aberto');

  useEffect(() => {
    const prefSalvas = JSON.parse(localStorage.getItem('preferenciasUsuario'));
    if (prefSalvas) {
      if (prefSalvas.tema) setTema(prefSalvas.tema);
      if (prefSalvas.notifSistema !== undefined) setNotifSistema(prefSalvas.notifSistema);
      if (prefSalvas.notifEstoque !== undefined) setNotifEstoque(prefSalvas.notifEstoque);
      if (prefSalvas.notifValidade !== undefined) setNotifValidade(prefSalvas.notifValidade);
      if (prefSalvas.produtosPorPagina) setProdutosPorPagina(prefSalvas.produtosPorPagina);
      if (prefSalvas.mostrarInativos !== undefined) setMostrarInativos(prefSalvas.mostrarInativos);
      if (prefSalvas.menuLateral) setMenuLateral(prefSalvas.menuLateral);
    }
  }, []);

  // 2. APLICADOR DE TEMA: Altera as cores do documento de forma global
  useEffect(() => {
    if (tema === 'escuro') {
      document.body.style.backgroundColor = '#1a1a1a';
      document.body.style.color = '#f1f1f1';
    } else if (tema === 'claro') {
      document.body.style.backgroundColor = '#ffffff';
      document.body.style.color = '#2c3e50';
    } else {
      document.body.style.backgroundColor = '#f4f6f8';
      document.body.style.color = '#2c3e50';
    }
  }, [tema]);

  const handleSalvar = () => {
    const preferencias = {
      tema, notifSistema, notifEstoque, notifValidade,
      produtosPorPagina, mostrarInativos, menuLateral
    };
    localStorage.setItem('preferenciasUsuario', JSON.stringify(preferencias));
    
    // Dispara evento global para o Layout saber que o tema mudou
    window.dispatchEvent(new Event('preferenciasAtualizadas'));
    alert('✅ Preferências salvas com sucesso!');
  };

  const handleRestaurar = () => {
    if (window.confirm("Deseja restaurar as configurações originais?")) {
      setTema('sistema');
      setNotifSistema(true);
      setNotifEstoque(true);
      setNotifValidade(true);
      setProdutosPorPagina('10');
      setMostrarInativos(false);
      setMenuLateral('aberto');
    }
  };

  const styles = {
    container: { display: 'flex', flexDirection: 'column', gap: '20px', color: '#2c3e50', maxWidth: '800px', margin: '0 auto', paddingBottom: '40px' },
    header: { marginBottom: '10px' },
    title: { margin: 0, fontSize: '24px', display: 'flex', alignItems: 'center', gap: '10px', color: tema === 'escuro' ? '#fff' : '#2c3e50' },
    subtitle: { color: '#7f8c8d', fontSize: '14px', marginTop: '5px' },
    card: { backgroundColor: tema === 'escuro' ? '#2c3e50' : '#fff', borderRadius: '8px', boxShadow: '0 2px 5px rgba(0,0,0,0.05)', border: '1px solid #ecf0f1', overflow: 'hidden', marginBottom: '20px' },
    cardHeader: { backgroundColor: tema === 'escuro' ? '#34495e' : '#f9fbfb', padding: '15px 20px', borderBottom: '1px solid #ecf0f1', fontWeight: 'bold', fontSize: '16px', display: 'flex', alignItems: 'center', gap: '8px', color: tema === 'escuro' ? '#fff' : '#34495e' },
    cardBody: { padding: '20px' },
    themeContainer: { display: 'flex', gap: '20px' },
    themeCard: { flex: 1, padding: '20px', borderRadius: '8px', border: '2px solid', cursor: 'pointer', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '10px', transition: 'all 0.2s' },
    themeIcon: { fontSize: '24px' },
    themeText: { fontWeight: 'bold', fontSize: '14px', color: tema === 'escuro' ? '#fff' : '#333' },
    radioCircle: { width: '16px', height: '16px', borderRadius: '50%', border: '2px solid #bdc3c7', display: 'flex', justifyContent: 'center', alignItems: 'center' },
    radioInner: { width: '8px', height: '8px', borderRadius: '50%', backgroundColor: '#3498db' },
    configRow: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '12px 0', borderBottom: '1px solid #f1f2f6' },
    configRowLast: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '12px 0' },
    configInfo: { display: 'flex', flexDirection: 'column' },
    configLabel: { fontWeight: 'bold', color: tema === 'escuro' ? '#fff' : '#2c3e50', fontSize: '14px' },
    select: { padding: '8px 12px', border: '1px solid #bdc3c7', borderRadius: '4px', outline: 'none', backgroundColor: '#fff', fontSize: '14px', cursor: 'pointer' },
    actions: { display: 'flex', justifyContent: 'flex-end', gap: '15px', marginTop: '10px' },
    btnRestore: { padding: '10px 20px', backgroundColor: 'transparent', color: '#7f8c8d', border: '1px solid #bdc3c7', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold', fontSize: '14px' },
    btnSave: { padding: '10px 30px', backgroundColor: '#3498db', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold', fontSize: '14px' }
  };

  const ThemeOption = ({ type, icon, label }) => {
    const isSelected = tema === type;
    return (
      <div 
        style={{...styles.themeCard, borderColor: isSelected ? '#3498db' : '#ecf0f1', backgroundColor: isSelected ? (tema === 'escuro' ? '#34495e' : '#f0f8ff') : 'transparent'}}
        onClick={() => setTema(type)}
      >
        <div style={styles.themeIcon}>{icon}</div>
        <div style={styles.themeText}>{label}</div>
        <div style={{...styles.radioCircle, borderColor: isSelected ? '#3498db' : '#bdc3c7'}}>
          {isSelected && <div style={styles.radioInner} />}
        </div>
      </div>
    );
  };

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h1 style={styles.title}>⚙️ Preferências</h1>
        <div style={styles.subtitle}>Personalize sua experiência no Café Caseiro ERP.</div>
      </div>

      <div style={styles.card}>
        <div style={styles.cardHeader}>🎨 Aparência</div>
        <div style={styles.cardBody}>
          <div style={styles.themeContainer}>
            <ThemeOption type="claro" icon="☀️" label="Claro" />
            <ThemeOption type="sistema" icon="🖥️" label="Sistema" />
            <ThemeOption type="escuro" icon="🌙" label="Escuro" />
          </div>
        </div>
      </div>

      <div style={styles.card}>
        <div style={styles.cardHeader}>📦 Preferências de Exibição</div>
        <div style={styles.cardBody}>
          <div style={styles.configRow}>
            <div style={styles.configInfo}>
              <span style={styles.configLabel}>Produtos por página</span>
            </div>
            <select style={styles.select} value={produtosPorPagina} onChange={(e) => setProdutosPorPagina(e.target.value)}>
              <option value="10">10</option>
              <option value="25">25</option>
              <option value="50">50</option>
            </select>
          </div>
          <div style={styles.configRow}>
            <div style={styles.configInfo}>
              <span style={styles.configLabel}>Mostrar produtos inativos</span>
            </div>
            <ToggleSwitch checked={mostrarInativos} onChange={setMostrarInativos} />
          </div>
          <div style={styles.configRowLast}>
            <div style={styles.configInfo}>
              <span style={styles.configLabel}>Menu lateral</span>
            </div>
            <select style={styles.select} value={menuLateral} onChange={(e) => setMenuLateral(e.target.value)}>
              <option value="aberto">Aberto</option>
              <option value="fechado">Fechado</option>
            </select>
          </div>
        </div>
      </div>

      <div style={styles.actions}>
        <button style={styles.btnRestore} onClick={handleRestaurar}>Restaurar Padrões</button>
        <button style={styles.btnSave} onClick={handleSalvar}>Salvar Preferências</button>
      </div>
    </div>
  );
};

export default PreferenciasManager;