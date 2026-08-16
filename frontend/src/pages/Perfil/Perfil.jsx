import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const Perfil = () => {
  const navigate = useNavigate();

  const [nome, setNome] = useState(() => localStorage.getItem('nomeUsuario') || '');
  const [email, setEmail] = useState(() => localStorage.getItem('emailUsuario') || '');
  const [foto, setFoto] = useState(() => localStorage.getItem('fotoUsuario') || ''); // Novo estado da Foto
  const [role, setRole] = useState('ADMIN');
  const [ultimoAcesso, setUltimoAcesso] = useState('');
  const [status, setStatus] = useState({ tipo: '', texto: '' });

  useEffect(() => {
    const dataAtual = new Date().toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });
    setUltimoAcesso(dataAtual);
    // Sua lógica de fetch (mantida igual) ...
  }, []);

  // FUNÇÃO NOVA: Lida com a escolha da imagem e converte para texto (Base64)
  const handleFotoChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setFoto(reader.result);
        localStorage.setItem('fotoUsuario', reader.result);
        // Avisa o Layout para atualizar a foto do Header na mesma hora
        window.dispatchEvent(new Event('fotoUsuarioAtualizada'));
      };
      reader.readAsDataURL(file); // Converte
    }
  };

  const handleSalvar = (e) => {
    e.preventDefault();
    setStatus({ tipo: '', texto: '' });

    localStorage.setItem('nomeUsuario', nome);
    localStorage.setItem('emailUsuario', email);
    window.dispatchEvent(new Event('fotoUsuarioAtualizada')); // Força a atualização do nome no header

    // Sua lógica de fetch PUT original vai aqui...
    setStatus({ tipo: 'sucesso', texto: 'Perfil atualizado com sucesso!' });
  };

  const styles = {
    container: { display: 'flex', flexDirection: 'column', gap: '20px', color: '#2c3e50', maxWidth: '700px', margin: '0 auto' },
    card: { backgroundColor: '#fff', padding: '30px 40px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)', border: '1px solid #ecf0f1', display: 'flex', flexDirection: 'column', alignItems: 'center' },
    title: { fontSize: '22px', fontWeight: 'bold', marginBottom: '25px', display: 'flex', alignItems: 'center', gap: '8px', color: '#2c3e50' },
    photoContainer: { display: 'flex', flexDirection: 'column', alignItems: 'center', marginBottom: '30px', cursor: 'pointer' },
    
    // Atualização do estilo da foto para receber a imagem de fundo
    photoCircle: { 
      width: '100px', height: '100px', borderRadius: '50%', 
      border: '2px dashed #bdc3c7', display: 'flex', justifyContent: 'center', 
      alignItems: 'center', fontSize: '32px', color: '#7f8c8d', marginBottom: '8px',
      backgroundImage: foto ? `url(${foto})` : 'none',
      backgroundSize: 'cover', backgroundPosition: 'center',
      overflow: 'hidden'
    },
    photoText: { fontSize: '13px', color: '#3498db', fontWeight: 'bold', cursor: 'pointer' },
    form: { width: '100%', display: 'flex', flexDirection: 'column', gap: '18px' },
    formGroup: { display: 'flex', flexDirection: 'row', alignItems: 'center', width: '100%' },
    label: { width: '140px', fontWeight: 'bold', color: '#34495e', fontSize: '14px' },
    input: { flex: 1, padding: '10px 14px', border: '1px solid #bdc3c7', borderRadius: '4px', fontSize: '14px', outline: 'none' },
    readOnlyValue: { flex: 1, padding: '10px 0', fontSize: '14px', color: '#2c3e50', fontWeight: '500' },
    actions: { display: 'flex', justifyContent: 'center', marginTop: '15px' },
    btnSalvar: { padding: '10px 40px', backgroundColor: '#3498db', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold', fontSize: '14px' },
    msgSucesso: { color: '#27ae60', fontSize: '14px', marginBottom: '10px', textAlign: 'center' }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <div style={styles.title}>👤 Meu Perfil</div>

        {/* INPUT DE FOTO OCULTO DENTRO DE UMA LABEL */}
        <div style={styles.photoContainer}>
          <label style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', cursor: 'pointer' }}>
            <div style={styles.photoCircle}>
              {!foto && '📷'}
            </div>
            <span style={styles.photoText}>Alterar Foto</span>
            <input 
              type="file" 
              accept="image/*" 
              style={{ display: 'none' }} 
              onChange={handleFotoChange} 
            />
          </label>
        </div>

        {status.texto && <div style={styles.msgSucesso}>{status.texto}</div>}

        <form style={styles.form} onSubmit={handleSalvar}>
          <div style={styles.formGroup}>
            <label style={styles.label}>Nome</label>
            <input type="text" style={styles.input} value={nome} onChange={(e) => setNome(e.target.value)} required />
          </div>
          <div style={styles.formGroup}>
            <label style={styles.label}>E-mail</label>
            <input type="email" style={styles.input} value={email} onChange={(e) => setEmail(e.target.value)} required />
          </div>
          <div style={styles.formGroup}>
            <label style={styles.label}>Cargo</label>
            <div style={styles.readOnlyValue}>{role === 'ADMIN' ? 'Administrador' : role}</div>
          </div>
          <div style={styles.formGroup}>
            <label style={styles.label}>Último acesso</label>
            <div style={styles.readOnlyValue}>{ultimoAcesso}</div>
          </div>
          <div style={styles.actions}>
            <button type="submit" style={styles.btnSalvar}>Salvar</button>
          </div>
        </form>

      </div>
    </div>
  );
};

export default Perfil;