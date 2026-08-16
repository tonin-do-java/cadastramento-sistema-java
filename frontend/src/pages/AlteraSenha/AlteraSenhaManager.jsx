import React, { useState } from 'react';

const AlteraSenhaManager = () => {
  // Estados para os campos de senha
  const [senhaAtual, setSenhaAtual] = useState('');
  const [novaSenha, setNovaSenha] = useState('');
  const [confirmacaoSenha, setConfirmacaoSenha] = useState('');

  // Estados para controlar a visibilidade das senhas (ícone de olho)
  const [showSenhaAtual, setShowSenhaAtual] = useState(false);
  const [showNovaSenha, setShowNovaSenha] = useState(false);
  const [showConfirmacao, setShowConfirmacao] = useState(false);

  // Estados de feedback
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState('');
  const [sucesso, setSucesso] = useState('');

  // Função para calcular a força da senha
  const calcularForcaSenha = (senha) => {
    let forca = 0;
    if (senha.length >= 8) forca += 1;
    if (senha.length >= 12) forca += 1;
    if (/[A-Z]/.test(senha)) forca += 1;
    if (/[0-9]/.test(senha)) forca += 1;
    if (/[^A-Za-z0-9]/.test(senha)) forca += 1;
    return forca;
  };

  const forca = calcularForcaSenha(novaSenha);
  let textoForca = 'Muito Fraca';
  let corForca = '#e74c3c'; // Vermelho
  let larguraForca = '20%';

  if (forca >= 4) { textoForca = 'Forte'; corForca = '#2ecc71'; larguraForca = '100%'; } // Verde
  else if (forca >= 3) { textoForca = 'Boa'; corForca = '#f1c40f'; larguraForca = '70%'; } // Amarelo
  else if (forca >= 2) { textoForca = 'Razoável'; corForca = '#e67e22'; larguraForca = '40%'; } // Laranja

  // Estilos
  const styles = {
    container: { display: 'flex', flexDirection: 'column', gap: '20px', color: '#2c3e50', maxWidth: '600px', margin: '0 auto', paddingBottom: '40px' },
    header: { marginBottom: '10px' },
    title: { margin: 0, fontSize: '24px', display: 'flex', alignItems: 'center', gap: '10px', color: '#2c3e50' },
    subtitle: { color: '#7f8c8d', fontSize: '14px', marginTop: '5px' },
    
    card: { backgroundColor: '#fff', borderRadius: '8px', boxShadow: '0 2px 5px rgba(0,0,0,0.05)', border: '1px solid #ecf0f1', overflow: 'hidden' },
    cardHeader: { backgroundColor: '#f9fbfb', padding: '15px 20px', borderBottom: '1px solid #ecf0f1', fontWeight: 'bold', fontSize: '16px', display: 'flex', alignItems: 'center', gap: '8px', color: '#34495e' },
    cardBody: { padding: '25px', display: 'flex', flexDirection: 'column', gap: '20px' },

    inputGroup: { display: 'flex', flexDirection: 'column', gap: '8px' },
    label: { fontSize: '14px', fontWeight: 'bold', color: '#34495e' },
    inputWrapper: { position: 'relative', display: 'flex', alignItems: 'center' },
    input: { width: '100%', padding: '10px 40px 10px 12px', border: '1px solid #bdc3c7', borderRadius: '4px', fontSize: '14px', outline: 'none', boxSizing: 'border-box' },
    eyeIcon: { position: 'absolute', right: '12px', cursor: 'pointer', color: '#7f8c8d', userSelect: 'none' },

    strengthMeterContainer: { display: 'flex', alignItems: 'center', gap: '10px', marginTop: '5px' },
    strengthBarBackground: { height: '8px', flex: 1, backgroundColor: '#ecf0f1', borderRadius: '4px', overflow: 'hidden' },
    strengthBarFill: { height: '100%', width: novaSenha.length > 0 ? larguraForca : '0%', backgroundColor: corForca, transition: 'width 0.3s ease, background-color 0.3s ease' },
    strengthText: { fontSize: '12px', fontWeight: 'bold', color: novaSenha.length > 0 ? corForca : '#7f8c8d', minWidth: '70px' },

    actions: { display: 'flex', justifyContent: 'flex-end', gap: '15px', marginTop: '10px' },
    btnCancel: { padding: '10px 20px', backgroundColor: '#fff', color: '#7f8c8d', border: '1px solid #bdc3c7', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold', fontSize: '14px' },
    btnSubmit: { padding: '10px 30px', backgroundColor: '#3498db', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold', fontSize: '14px', opacity: loading ? 0.7 : 1 },
    
    alertError: { padding: '12px', backgroundColor: '#fdecea', color: '#e74c3c', borderRadius: '4px', fontSize: '14px', border: '1px solid #fadbd8' },
    alertSuccess: { padding: '12px', backgroundColor: '#eafaf1', color: '#27ae60', borderRadius: '4px', fontSize: '14px', border: '1px solid #d5f5e3' }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErro('');
    setSucesso('');

    // Validações no Front-end
    if (!senhaAtual || !novaSenha || !confirmacaoSenha) {
      return setErro('Por favor, preencha todos os campos.');
    }
    if (novaSenha !== confirmacaoSenha) {
      return setErro('A nova senha e a confirmação não coincidem.');
    }
    if (novaSenha.length < 8) {
      return setErro('A nova senha deve ter no mínimo 8 caracteres.');
    }

    setLoading(true);

    try {
      // Recupera o token JWT salvo no login
      const token = localStorage.getItem('tokenJWT'); 

      // Monta o payload esperado pelo backend
      // OBS: Seu back-end usa o UsuarioCreateRequestDto, que herda de UsuarioRequestDto.
      // Como o endpoint só processa "senha" e "confirmacaoSenha", enviamos apenas esses dados.
      const payload = {
        senha: novaSenha,
        confirmacaoSenha: confirmacaoSenha,
        // Caso seu back-end exija os campos herdados por conta do @Valid, descomente e preencha com dados fictícios ou os dados reais do usuário logado:
        // nome: "Usuário Logado",
        // email: "email@logado.com",
        // role: "ADMIN"
      };

      const response = await fetch('http://localhost:8080/api/auth/esqueciSenha', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        setSucesso('Senha alterada com sucesso!');
        setSenhaAtual('');
        setNovaSenha('');
        setConfirmacaoSenha('');
      } else {
        const errData = await response.json().catch(() => null);
        setErro(errData?.message || 'Erro ao alterar a senha. Verifique os dados e tente novamente.');
      }
    } catch (error) {
      setErro('Erro de conexão com o servidor.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      
      {/* Cabeçalho */}
      <div style={styles.header}>
        <h1 style={styles.title}>🔐 Alterar Senha</h1>
        <div style={styles.subtitle}>Atualize sua senha de acesso ao sistema.</div>
      </div>

      {/* Alertas */}
      {erro && <div style={styles.alertError}>❌ {erro}</div>}
      {sucesso && <div style={styles.alertSuccess}>✅ {sucesso}</div>}

      {/* Cartão Principal */}
      <div style={styles.card}>
        <div style={styles.cardHeader}>🔐 Segurança da Conta</div>
        
        <form style={styles.cardBody} onSubmit={handleSubmit}>
          
          {/* Senha Atual */}
          <div style={styles.inputGroup}>
            <label style={styles.label}>Senha atual</label>
            <div style={styles.inputWrapper}>
              <input 
                type={showSenhaAtual ? "text" : "password"} 
                style={styles.input} 
                placeholder="••••••••"
                value={senhaAtual}
                onChange={(e) => setSenhaAtual(e.target.value)}
              />
              <span style={styles.eyeIcon} onClick={() => setShowSenhaAtual(!showSenhaAtual)}>
                {showSenhaAtual ? '🙈' : '👁️'}
              </span>
            </div>
          </div>

          {/* Nova Senha */}
          <div style={styles.inputGroup}>
            <label style={styles.label}>Nova senha</label>
            <div style={styles.inputWrapper}>
              <input 
                type={showNovaSenha ? "text" : "password"} 
                style={styles.input} 
                placeholder="••••••••"
                value={novaSenha}
                onChange={(e) => setNovaSenha(e.target.value)}
              />
              <span style={styles.eyeIcon} onClick={() => setShowNovaSenha(!showNovaSenha)}>
                {showNovaSenha ? '🙈' : '👁️'}
              </span>
            </div>
            
            {/* Medidor de Força */}
            <div style={styles.strengthMeterContainer}>
              <span style={{ fontSize: '12px', color: '#7f8c8d' }}>Força da senha:</span>
              <div style={styles.strengthBarBackground}>
                <div style={styles.strengthBarFill}></div>
              </div>
              <span style={styles.strengthText}>{novaSenha.length > 0 ? textoForca : ''}</span>
            </div>
          </div>

          {/* Confirmar Nova Senha */}
          <div style={styles.inputGroup}>
            <label style={styles.label}>Confirmar nova senha</label>
            <div style={styles.inputWrapper}>
              <input 
                type={showConfirmacao ? "text" : "password"} 
                style={styles.input} 
                placeholder="••••••••"
                value={confirmacaoSenha}
                onChange={(e) => setConfirmacaoSenha(e.target.value)}
              />
              <span style={styles.eyeIcon} onClick={() => setShowConfirmacao(!showConfirmacao)}>
                {showConfirmacao ? '🙈' : '👁️'}
              </span>
            </div>
          </div>

          {/* Botões */}
          <div style={styles.actions}>
            <button type="button" style={styles.btnCancel} onClick={() => window.history.back()}>
              Cancelar
            </button>
            <button type="submit" style={styles.btnSubmit} disabled={loading}>
              {loading ? 'Aguarde...' : 'Alterar Senha'}
            </button>
          </div>

        </form>
      </div>
    </div>
  );
};

export default AlteraSenhaManager;