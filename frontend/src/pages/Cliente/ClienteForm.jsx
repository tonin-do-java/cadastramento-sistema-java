import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Layout from '../../pages/Layout/Layout.jsx';

const ClienteForm = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditing = Boolean(id);

  const [formData, setFormData] = useState({
    tipoPessoa: 'JURIDICA',
    nome: '',
    nomeFantasia: '',
    documento: '',
    inscricaoEstadual: '',
    telefone: '',
    celular: '',
    email: '',
    cep: '',
    logradouro: '',
    numero: '',
    bairro: '',
    cidade: '',
    estado: 'GO',
  });

  useEffect(() => {
    if (isEditing) {
      carregarCliente();
    }
  }, [id]);

  const carregarCliente = async () => {
    try {
      const token = localStorage.getItem('tokenJWT'); // Pegando o token para o GET
      
      const response = await fetch(`/api/cliente/${id}`, {
        headers: {
          'Authorization': `Bearer ${token}` // Inserindo o token para evitar o 403
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        setFormData({
          tipoPessoa: data.tipoPessoa || 'JURIDICA',
          nome: data.nome || '',
          nomeFantasia: data.nomeFantasia || '',
          documento: data.documento || '',
          inscricaoEstadual: data.inscricaoEstadual || '',
          telefone: data.contato?.telefone || '',
          celular: data.contato?.celular || '',
          email: data.contato?.email || '',
          pessoaContato: '',
          cep: data.endereco?.cep || '',
          logradouro: data.endereco?.logradouro || '',
          numero: data.endereco?.numero || '',
          bairro: data.endereco?.bairro || '',
          cidade: data.endereco?.cidade || '',
          estado: data.endereco?.estado || 'GO',
        });
      }
    } catch (error) {
      console.error("Erro ao buscar dados do cliente:", error);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Montando o objeto exatamente na estrutura que o Spring Boot espera
    const payload = {
      nome: formData.nome,
      tipoPessoa: formData.tipoPessoa,
      documento: formData.documento,
      nomeFantasia: formData.tipoPessoa === 'JURIDICA' ? formData.nomeFantasia : null,
      inscricaoEstadual: formData.tipoPessoa === 'JURIDICA' ? formData.inscricaoEstadual : null,
      
      endereco: {
        cep: formData.cep,
        logradouro: formData.logradouro,
        numero: formData.numero,
        bairro: formData.bairro,
        cidade: formData.cidade,
        estado: formData.estado
      },
      
      contato: {
        telefone: formData.telefone,
        celular: formData.celular,
        email: formData.email
      }
    };

    try {
      const method = isEditing ? 'PUT' : 'POST';
      const url = isEditing ? `/api/cliente/${id}` : '/api/cliente';
      const token = localStorage.getItem('tokenJWT'); 

      const response = await fetch(url, {
        method,
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}` 
        },
        body: JSON.stringify(payload) // Enviando o payload formatado corretamente
      });

      if (response.ok) {
        navigate('/clientes');
      } else {
        const errorData = await response.json().catch(() => null);
        console.error("Erros de validação:", errorData);
        alert('Erro ao salvar cliente. Verifique os dados inseridos.');
      }
    } catch (error) {
      console.error("Erro no cadastro:", error);
    }
  };

  const styles = {
    card: { backgroundColor: '#fff', borderRadius: '8px', padding: '25px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)', marginBottom: '20px' },
    sectionHeader: { color: '#2c3e50', fontSize: '13px', fontWeight: 'bold', borderBottom: '1px solid #eee', paddingBottom: '8px', marginBottom: '15px', textTransform: 'uppercase', letterSpacing: '0.5px' },
    row: { display: 'flex', gap: '20px', marginBottom: '15px', flexWrap: 'wrap' },
    formGroup: { display: 'flex', flexDirection: 'column', flex: 1, minWidth: '200px' },
    label: { fontSize: '13px', color: '#333', marginBottom: '5px', fontWeight: 'bold' },
    input: { padding: '10px', borderRadius: '6px', border: '1px solid #ccc', fontSize: '14px' },
    select: { padding: '10px', borderRadius: '6px', border: '1px solid #ccc', fontSize: '14px', backgroundColor: '#fff' },
    btnCancel: { backgroundColor: '#ecf0f1', color: '#7f8c8d', border: 'none', padding: '10px 20px', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold', marginRight: '10px' },
    btnSave: { backgroundColor: '#3498db', color: '#fff', border: 'none', padding: '10px 25px', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }
  };

  return (
    <Layout>
      <div style={{ ...styles.card, marginBottom: '20px' }}>
        <h2 style={{ margin: 0, color: '#2c3e50' }}>👥 {isEditing ? 'Editar Cliente' : 'Novo Cliente'}</h2>
        <p style={{ margin: '5px 0 0 0', color: '#7f8c8d', fontSize: '14px' }}>
          {isEditing ? 'Atualize as informações do cliente.' : 'Cadastre um novo cliente no sistema.'}
        </p>
      </div>

      <form onSubmit={handleSubmit}>
        {/* DADOS DO CLIENTE */}
        <div style={styles.card}>
          <div style={styles.sectionHeader}>DADOS DO CLIENTE</div>

          <div style={styles.row}>
            <div style={styles.formGroup}>
              <label style={styles.label}>Tipo de Cliente</label>
              <select style={styles.select} name="tipoPessoa" value={formData.tipoPessoa} onChange={handleChange}>
                <option value="JURIDICA">Empresa (PJ)</option>
                <option value="FISICA">Pessoa Física (PF)</option>
              </select>
            </div>
          </div>

          <div style={styles.row}>
            <div style={styles.formGroup}>
              <label style={styles.label}>Razão Social / Nome</label>
              <input style={styles.input} type="text" name="nome" value={formData.nome} onChange={handleChange} required />
            </div>
          </div>

          {/* Renderização condicional: Mostra Nome Fantasia Apenas se for PJ */}
          {formData.tipoPessoa === 'JURIDICA' && (
            <div style={styles.row}>
              <div style={styles.formGroup}>
                <label style={styles.label}>Nome Fantasia</label>
                <input style={styles.input} type="text" name="nomeFantasia" value={formData.nomeFantasia} onChange={handleChange} />
              </div>
            </div>
          )}

          <div style={styles.row}>
            <div style={styles.formGroup}>
              <label style={styles.label}>{formData.tipoPessoa === 'JURIDICA' ? 'CNPJ' : 'CPF'}</label>
              <input style={styles.input} type="text" name="documento" value={formData.documento} onChange={handleChange} required />
            </div>
            
            {/* Renderização condicional: Mostra Inscrição Estadual Apenas se for PJ */}
            {formData.tipoPessoa === 'JURIDICA' && (
              <div style={styles.formGroup}>
                <label style={styles.label}>Inscrição Estadual</label>
                <input style={styles.input} type="text" name="inscricaoEstadual" value={formData.inscricaoEstadual} onChange={handleChange} />
              </div>
            )}
          </div>
        </div>

        {/* CONTATO */}
        <div style={styles.card}>
          <div style={styles.sectionHeader}>CONTATO</div>

          <div style={styles.row}>
            <div style={styles.formGroup}>
              <label style={styles.label}>Telefone</label>
              <input style={styles.input} type="text" name="telefone" value={formData.telefone} onChange={handleChange} />
            </div>
            <div style={styles.formGroup}>
              <label style={styles.label}>Celular</label>
              <input style={styles.input} type="text" name="celular" value={formData.celular} onChange={handleChange} />
            </div>
          </div>

          <div style={styles.row}>
            <div style={styles.formGroup}>
              <label style={styles.label}>E-mail</label>
              <input style={styles.input} type="email" name="email" value={formData.email} onChange={handleChange} />
            </div>
          </div>
        </div>

        {/* ENDEREÇO */}
        <div style={styles.card}>
          <div style={styles.sectionHeader}>ENDEREÇO</div>

          <div style={styles.row}>
            <div style={{ ...styles.formGroup, flex: '0 0 200px' }}>
              <label style={styles.label}>CEP</label>
              <input style={styles.input} type="text" name="cep" value={formData.cep} onChange={handleChange} />
            </div>
          </div>

          <div style={styles.row}>
            <div style={styles.formGroup}>
              <label style={styles.label}>Endereço</label>
              <input style={styles.input} type="text" name="logradouro" value={formData.logradouro} onChange={handleChange} />
            </div>
          </div>

          <div style={styles.row}>
            <div style={{ ...styles.formGroup, flex: '0 0 120px' }}>
              <label style={styles.label}>Número</label>
              <input style={styles.input} type="text" name="numero" value={formData.numero} onChange={handleChange} />
            </div>
            <div style={styles.formGroup}>
              <label style={styles.label}>Bairro</label>
              <input style={styles.input} type="text" name="bairro" value={formData.bairro} onChange={handleChange} />
            </div>
          </div>

          <div style={styles.row}>
            <div style={styles.formGroup}>
              <label style={styles.label}>Cidade</label>
              <input style={styles.input} type="text" name="cidade" value={formData.cidade} onChange={handleChange} />
            </div>
            <div style={{ ...styles.formGroup, flex: '0 0 120px' }}>
              <label style={styles.label}>Estado</label>
              <select style={styles.select} name="estado" value={formData.estado} onChange={handleChange}>
                <option value="GO">GO</option>
                <option value="SP">SP</option>
                <option value="MG">MG</option>
                <option value="DF">DF</option>
              </select>
            </div>
          </div>
        </div>

        <div style={styles.card}>

          <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '20px' }}>
            <button type="button" style={styles.btnCancel} onClick={() => navigate('/clientes')}>Cancelar</button>
            <button type="submit" style={styles.btnSave}>Salvar Cliente</button>
          </div>
        </div>
      </form>
    </Layout>
  );
};

export default ClienteForm;