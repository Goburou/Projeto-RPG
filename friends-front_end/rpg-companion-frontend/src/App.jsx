// App.js
import React, { useState, useEffect } from 'react';

// Adicione o link para o Tailwind CSS (geralmente no index.html ou no cabeçalho do React)
// Para o ambiente de Canvas, Tailwind CSS é injetado automaticamente se você usar as classes.
// Para um projeto React real, você precisaria configurar o Tailwind em seu projeto.

function App() {
  // Estado para controlar qual página está visível: 'campaigns' ou 'characters'
  const [currentPage, setCurrentPage] = useState('campaigns');
  // Estado para armazenar o ID da campanha selecionada ao navegar para a página de personagens
  const [selectedCampaignId, setSelectedCampaignId] = useState(null);
  // Estado para armazenar o nome da campanha selecionada para exibição
  const [selectedCampaignName, setSelectedCampaignName] = useState('');
  // ID de usuário mockado para autenticação (deve corresponder ao seu AuthServiceImpl no backend)
  const [currentUserId, setCurrentUserId] = useState('dummy_authenticated_user_id');

  /**
   * Componente para exibir e gerenciar campanhas.
   * @param {function} onSelectCampaign - Callback para selecionar uma campanha e navegar para a página de personagens.
   */
  const CampaignsPage = ({ onSelectCampaign }) => {
    const [campaigns, setCampaigns] = useState([]);
    const [newCampaignName, setNewCampaignName] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Efeito para buscar as campanhas quando o componente é montado
    useEffect(() => {
      fetchCampaigns();
    }, []); // Array de dependências vazio para executar apenas uma vez na montagem

    // Função para buscar campanhas do backend
    const fetchCampaigns = async () => {
      setLoading(true);
      setError(null); // Limpa erros anteriores
      try {
        const response = await fetch('http://localhost:8080/api/campaigns', {
          headers: {
            // Inclui o cabeçalho X-User-ID para a autenticação simples do backend
            'X-User-ID': currentUserId,
          },
        });
        if (!response.ok) {
          // Se a resposta não for OK (status 4xx ou 5xx), lança um erro
          const errorText = await response.text(); // Tenta ler a mensagem de erro do corpo da resposta
          throw new Error(`HTTP error! Status: ${response.status} - ${errorText}`);
        }
        const data = await response.json(); // Analisa a resposta JSON
        setCampaigns(data);
      } catch (e) {
        // Captura e exibe qualquer erro que ocorra durante a requisição
        setError(`Falha ao carregar campanhas: ${e.message}`);
        console.error("Erro ao buscar campanhas:", e);
      } finally {
        setLoading(false); // Finaliza o estado de carregamento
      }
    };

    // Função para criar uma nova campanha
    const handleCreateCampaign = async () => {
      if (!newCampaignName.trim()) { // Valida se o nome não está vazio
        setError("O nome da campanha não pode ser vazio.");
        return;
      }
      setError(null);
      try {
        const response = await fetch('http://localhost:8080/api/campaigns', {
          method: 'POST', // Método HTTP POST para criar
          headers: {
            'Content-Type': 'application/json', // Informa que o corpo é JSON
            'X-User-ID': currentUserId,
          },
          body: JSON.stringify({ name: newCampaignName }), // Converte o objeto para JSON
        });
        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(`Falha ao criar campanha: ${errorText}`);
        }
        await response.text(); // O backend retorna o ID, podemos apenas ler ou ignorar
        setNewCampaignName(''); // Limpa o campo de input
        fetchCampaigns(); // Atualiza a lista de campanhas após a criação
      } catch (e) {
        setError(`Erro ao criar campanha: ${e.message}`);
        console.error("Erro ao criar campanha:", e);
      }
    };

    return (
      <div className="p-4 bg-gray-100 rounded-xl shadow-inner">
        <h2 className="text-2xl font-bold mb-4 text-gray-800">Suas Campanhas</h2>
        <div className="mb-6 flex flex-col sm:flex-row space-y-2 sm:space-y-0 sm:space-x-2">
          <input
            type="text"
            placeholder="Nome da nova campanha"
            className="flex-grow p-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500 text-gray-800"
            value={newCampaignName}
            onChange={(e) => setNewCampaignName(e.target.value)}
          />
          <button
            onClick={handleCreateCampaign}
            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors shadow-md text-nowrap"
          >
            Criar Campanha
          </button>
        </div>

        {loading && <p className="text-blue-600">Carregando campanhas...</p>}
        {error && <p className="text-red-600">Erro: {error}</p>}

        <ul className="space-y-3">
          {campaigns.length === 0 && !loading && !error && <p className="text-gray-600">Nenhuma campanha encontrada. Crie uma!</p>}
          {campaigns.map((campaign) => (
            <li
              key={campaign.id}
              className="flex flex-col sm:flex-row justify-between items-start sm:items-center bg-white p-3 rounded-md shadow-sm border border-gray-200"
            >
              <span className="text-gray-700 font-medium mb-2 sm:mb-0">{campaign.name}</span>
              <button
                onClick={() => onSelectCampaign(campaign.id, campaign.name)}
                className="px-3 py-1 bg-green-500 text-white rounded-md text-sm hover:bg-green-600 transition-colors shadow-sm"
              >
                Ver Personagens
              </button>
            </li>
          ))}
        </ul>
      </div>
    );
  };

  /**
   * Componente para exibir e gerenciar personagens de uma campanha específica.
   * @param {string} campaignId - O ID da campanha.
   * @param {string} campaignName - O nome da campanha.
   * @param {function} onBackToCampaigns - Callback para voltar à página de campanhas.
   */
  const CharactersPage = ({ campaignId, campaignName, onBackToCampaigns }) => {
    const [characters, setCharacters] = useState([]);
    const [newCharacterName, setNewCharacterName] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Efeito para buscar os personagens quando o campaignId muda
    useEffect(() => {
      if (campaignId) {
        fetchCharacters();
      }
    }, [campaignId]); // Depende do campaignId para recarregar quando a campanha é alterada

    // Função para buscar personagens do backend
    const fetchCharacters = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await fetch(`http://localhost:8080/api/campaigns/${campaignId}/characters`, {
          headers: {
            'X-User-ID': currentUserId,
          },
        });
        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(`HTTP error! Status: ${response.status} - ${errorText}`);
        }
        const data = await response.json();
        setCharacters(data);
      } catch (e) {
        setError(`Falha ao carregar personagens: ${e.message}`);
        console.error("Erro ao buscar personagens:", e);
      } finally {
        setLoading(false);
      }
    };

    // Função para criar um novo personagem
    const handleCreateCharacter = async () => {
      if (!newCharacterName.trim()) {
        setError("O nome do personagem não pode ser vazio.");
        return;
      }
      setError(null);
      try {
        const response = await fetch(`http://localhost:8080/api/campaigns/${campaignId}/characters`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'X-User-ID': currentUserId,
          },
          body: JSON.stringify({
            name: newCharacterName,
            system: "Custom RPG", // Valores padrão, você pode expandir isso para um formulário completo
            attributesJson: "{}",
            hp: 100,
            ac: 10,
            notes: "",
            customFieldsJson: "{}"
          }),
        });
        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(`Falha ao criar personagem: ${errorText}`);
        }
        await response.text(); // O backend retorna o ID, podemos apenas ler ou ignorar
        setNewCharacterName('');
        fetchCharacters(); // Atualiza a lista de personagens após a criação
      } catch (e) {
        setError(`Erro ao criar personagem: ${e.message}`);
        console.error("Erro ao criar personagem:", e);
      }
    };

    return (
      <div className="p-4 bg-gray-100 rounded-xl shadow-inner">
        <button
          onClick={onBackToCampaigns}
          className="mb-4 px-4 py-2 bg-gray-600 text-white rounded-md hover:bg-gray-700 transition-colors shadow-md"
        >
          &larr; Voltar para Campanhas
        </button>
        <h2 className="text-2xl font-bold mb-4 text-gray-800">Personagens da Campanha: {campaignName}</h2>

        <div className="mb-6 flex flex-col sm:flex-row space-y-2 sm:space-y-0 sm:space-x-2">
          <input
            type="text"
            placeholder="Nome do novo personagem"
            className="flex-grow p-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500 text-gray-800"
            value={newCharacterName}
            onChange={(e) => setNewCharacterName(e.target.value)}
          />
          <button
            onClick={handleCreateCharacter}
            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors shadow-md text-nowrap"
          >
            Criar Personagem
          </button>
        </div>

        {loading && <p className="text-blue-600">Carregando personagens...</p>}
        {error && <p className="text-red-600">Erro: {error}</p>}

        <ul className="space-y-3">
          {characters.length === 0 && !loading && !error && <p className="text-gray-600">Nenhum personagem encontrado. Crie um!</p>}
          {characters.map((character) => (
            <li
              key={character.id}
              className="bg-white p-3 rounded-md shadow-sm border border-gray-200"
            >
              <h3 className="font-semibold text-gray-800">{character.name}</h3>
              <p className="text-sm text-gray-600">Sistema: {character.system}</p>
              <p className="text-sm text-gray-600">HP: {character.hp} | AC: {character.ac}</p>
              {character.notes && <p className="text-sm text-gray-600">Notas: {character.notes}</p>}
              {/* Você pode adicionar mais detalhes ou botões de edição/exclusão aqui */}
            </li>
          ))}
        </ul>
      </div>
    );
  };


  // Renderiza a página apropriada com base no estado 'currentPage'
  const renderPage = () => {
    switch (currentPage) {
      case 'campaigns':
        return (
          <CampaignsPage
            onSelectCampaign={(id, name) => {
              setSelectedCampaignId(id);
              setSelectedCampaignName(name);
              setCurrentPage('characters'); // Mudar para a página de personagens
            }}
          />
        );
      case 'characters':
        if (!selectedCampaignId) {
          // Caso um erro ocorra e não haja campanha selecionada
          return (
            <div className="p-4 bg-red-100 text-red-800 rounded-lg shadow-inner">
              <p>Erro: Campanha não selecionada. Por favor, volte para a página de campanhas.</p>
              <button
                onClick={() => setCurrentPage('campaigns')}
                className="mt-4 px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 transition-colors shadow-md"
              >
                Voltar
              </button>
            </div>
          );
        }
        return (
          <CharactersPage
            campaignId={selectedCampaignId}
            campaignName={selectedCampaignName}
            onBackToCampaigns={() => {
              setSelectedCampaignId(null);
              setSelectedCampaignName('');
              setCurrentPage('campaigns'); // Voltar para a página de campanhas
            }}
          />
        );
      default:
        // Página padrão para casos inesperados
        return (
          <div className="p-4 bg-red-100 text-red-800 rounded-lg shadow-inner">
            <p>Página não encontrada.</p>
          </div>
        );
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 to-gray-700 text-white font-sans p-6 flex flex-col items-center">
      {/* Container principal para a aplicação */}
      <div className="w-full max-w-4xl bg-gray-800 rounded-xl shadow-2xl p-6">
        <h1 className="text-4xl font-extrabold text-center mb-8 text-blue-400">
          RPG Companion Frontend
        </h1>
        {renderPage()} {/* Renderiza a página atual */}
      </div>
    </div>
  );
}

export default App;
