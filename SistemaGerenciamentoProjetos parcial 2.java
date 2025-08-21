// ========== IMPORTS NO INÍCIO DO ARQUIVO ==========
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// ========== CAMADA DE MODELO ==========

// Enum para Status das Tarefas
enum StatusTarefa {
    PENDENTE("Pendente"),
    EM_ANDAMENTO("Em Andamento"),
    CONCLUIDA("Concluída"),
    ATRASADA("Atrasada"),
    CANCELADA("Cancelada");

    private String descricao;

    StatusTarefa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

// Classe Funcionario
class Funcionario {
    private int id;
    private String nome;
    private String cargo;
    private String email;
    private List<Tarefa> tarefasAtribuidas;
    private static int contadorId = 1;

    public Funcionario(String nome, String cargo, String email) {
        this.id = contadorId++;
        this.nome = nome;
        this.cargo = cargo;
        this.email = email;
        this.tarefasAtribuidas = new ArrayList<>();
    }

    // Getters e Setters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Tarefa> getTarefasAtribuidas() { return new ArrayList<>(tarefasAtribuidas); }

    // Métodos de negócio
    public void adicionarTarefa(Tarefa tarefa) {
        if (!tarefasAtribuidas.contains(tarefa)) {
            tarefasAtribuidas.add(tarefa);
        }
    }

    public void removerTarefa(Tarefa tarefa) {
        tarefasAtribuidas.remove(tarefa);
    }

    public int getQuantidadeTarefas() {
        return tarefasAtribuidas.size();
    }

    public int getTarefasConcluidas() {
        return (int) tarefasAtribuidas.stream()
            .filter(t -> t.getStatus() == StatusTarefa.CONCLUIDA)
            .count();
    }

    @Override
    public String toString() {
        return nome + " - " + cargo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Funcionario that = (Funcionario) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

// Classe Tarefa
class Tarefa {
    private int id;
    private String titulo;
    private String descricao;
    private StatusTarefa status;
    private Funcionario responsavel;
    private LocalDate dataInicio;
    private LocalDate dataPrazo;
    private int prioridade; // 1 = Alta, 2 = Média, 3 = Baixa
    private static int contadorId = 1;

    public Tarefa(String titulo, String descricao, LocalDate dataInicio, LocalDate dataPrazo) {
        this.id = contadorId++;
        this.titulo = titulo;
        this.descricao = descricao;
        this.status = StatusTarefa.PENDENTE;
        this.dataInicio = dataInicio;
        this.dataPrazo = dataPrazo;
        this.prioridade = 2;
        verificarAtraso();
    }

    // Getters e Setters
    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public StatusTarefa getStatus() { return status; }
    public void setStatus(StatusTarefa status) {
        this.status = status;
        verificarAtraso();
    }
    public Funcionario getResponsavel() { return responsavel; }
    public void setResponsavel(Funcionario responsavel) {
        if (this.responsavel != null) {
            this.responsavel.removerTarefa(this);
        }
        this.responsavel = responsavel;
        if (responsavel != null) {
            responsavel.adicionarTarefa(this);
        }
    }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataPrazo() { return dataPrazo; }
    public void setDataPrazo(LocalDate dataPrazo) { this.dataPrazo = dataPrazo; }
    public int getPrioridade() { return prioridade; }
    public void setPrioridade(int prioridade) { this.prioridade = prioridade; }

    // Métodos de negócio
    public void verificarAtraso() {
        if (status != StatusTarefa.CONCLUIDA && status != StatusTarefa.CANCELADA) {
            if (LocalDate.now().isAfter(dataPrazo)) {
                status = StatusTarefa.ATRASADA;
            }
        }
    }

    public String getPrioridadeString() {
        switch (prioridade) {
            case 1: return "Alta";
            case 2: return "Média";
            case 3: return "Baixa";
            default: return "Média";
        }
    }

    public String getDataPrazoFormatada() {
        return dataPrazo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @Override
    public String toString() {
        return titulo + " - " + status.getDescricao();
    }
}

// Classe Projeto
class Projeto {
    private int id;
    private String nome;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private List<Tarefa> tarefas;
    private List<Funcionario> equipe;
    private static int contadorId = 1;

    public Projeto(String nome, String descricao, LocalDate dataInicio, LocalDate dataFim) {
        this.id = contadorId++;
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.tarefas = new ArrayList<>();
        this.equipe = new ArrayList<>();
    }

    // Getters e Setters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    public List<Tarefa> getTarefas() { return new ArrayList<>(tarefas); }
    public List<Funcionario> getEquipe() { return new ArrayList<>(equipe); }

    // Métodos de negócio
    public void adicionarTarefa(Tarefa tarefa) {
        if (!tarefas.contains(tarefa)) {
            tarefas.add(tarefa);
        }
    }

    public void removerTarefa(Tarefa tarefa) {
        tarefas.remove(tarefa);
        if (tarefa.getResponsavel() != null) {
            tarefa.getResponsavel().removerTarefa(tarefa);
        }
    }

    public void adicionarFuncionario(Funcionario funcionario) {
        if (!equipe.contains(funcionario)) {
            equipe.add(funcionario);
        }
    }

    public void removerFuncionario(Funcionario funcionario) {
        equipe.remove(funcionario);
        // Remove funcionário das tarefas
        for (Tarefa tarefa : tarefas) {
            if (tarefa.getResponsavel() != null && tarefa.getResponsavel().equals(funcionario)) {
                tarefa.setResponsavel(null);
            }
        }
    }

    public double getProgressoPercentual() {
        if (tarefas.isEmpty()) return 0;
        long concluidas = tarefas.stream()
            .filter(t -> t.getStatus() == StatusTarefa.CONCLUIDA)
            .count();
        return (double) concluidas / tarefas.size() * 100;
    }

    public List<Tarefa> getTarefasPorStatus(StatusTarefa status) {
        return tarefas.stream()
            .filter(t -> t.getStatus() == status)
            .collect(Collectors.toList());
    }

    public List<Tarefa> getTarefasPorFuncionario(Funcionario funcionario) {
        return tarefas.stream()
            .filter(t -> funcionario.equals(t.getResponsavel()))
            .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return nome + " - " + String.format("%.1f%%", getProgressoPercentual()) + " concluído";
    }
}

// ========== SISTEMA DE GERENCIAMENTO ==========

class GerenciadorProjetos {
    private List<Projeto> projetos;
    private List<Funcionario> funcionarios;

    public GerenciadorProjetos() {
        this.projetos = new ArrayList<>();
        this.funcionarios = new ArrayList<>();
        inicializarDadosExemplo();
    }

    private void inicializarDadosExemplo() {
        // Criar funcionários
        Funcionario f1 = new Funcionario("João Silva", "Desenvolvedor Senior", "joao@empresa.com");
        Funcionario f2 = new Funcionario("Maria Santos", "Desenvolvedora Pleno", "maria@empresa.com");
        Funcionario f3 = new Funcionario("Pedro Oliveira", "Desenvolvedor Junior", "pedro@empresa.com");
        Funcionario f4 = new Funcionario("Ana Costa", "Analista de Sistemas", "ana@empresa.com");

        funcionarios.add(f1);
        funcionarios.add(f2);
        funcionarios.add(f3);
        funcionarios.add(f4);

        // Criar projeto
        Projeto p1 = new Projeto("Sistema E-commerce",
            "Desenvolvimento de plataforma de vendas online",
            LocalDate.now().minusDays(30),
            LocalDate.now().plusDays(60));

        p1.adicionarFuncionario(f1);
        p1.adicionarFuncionario(f2);
        p1.adicionarFuncionario(f3);
        p1.adicionarFuncionario(f4);

        // Criar tarefas
        Tarefa t1 = new Tarefa("Configurar Banco de Dados",
            "Criar estrutura inicial do banco de dados MySQL",
            LocalDate.now().minusDays(20),
            LocalDate.now().minusDays(10));
        t1.setStatus(StatusTarefa.CONCLUIDA);
        t1.setResponsavel(f1);
        t1.setPrioridade(1);

        Tarefa t2 = new Tarefa("Desenvolver API REST",
            "Implementar endpoints para produtos e pedidos",
            LocalDate.now().minusDays(15),
            LocalDate.now().plusDays(5));
        t2.setStatus(StatusTarefa.EM_ANDAMENTO);
        t2.setResponsavel(f1);
        t2.setPrioridade(1);

        Tarefa t3 = new Tarefa("Criar Interface de Login",
            "Desenvolver tela de autenticação de usuários",
            LocalDate.now().minusDays(10),
            LocalDate.now().plusDays(2));
        t3.setStatus(StatusTarefa.EM_ANDAMENTO);
        t3.setResponsavel(f2);
        t3.setPrioridade(2);

        Tarefa t4 = new Tarefa("Implementar Carrinho de Compras",
            "Funcionalidade de adicionar/remover produtos",
            LocalDate.now(),
            LocalDate.now().plusDays(15));
        t4.setStatus(StatusTarefa.PENDENTE);
        t4.setResponsavel(f3);
        t4.setPrioridade(2);

        Tarefa t5 = new Tarefa("Testes de Integração",
            "Realizar testes completos do sistema",
            LocalDate.now().plusDays(20),
            LocalDate.now().plusDays(30));
        t5.setStatus(StatusTarefa.PENDENTE);
        t5.setResponsavel(f4);
        t5.setPrioridade(3);

        p1.adicionarTarefa(t1);
        p1.adicionarTarefa(t2);
        p1.adicionarTarefa(t3);
        p1.adicionarTarefa(t4);
        p1.adicionarTarefa(t5);

        projetos.add(p1);

        // Criar segundo projeto
        Projeto p2 = new Projeto("App Mobile",
            "Aplicativo móvel para acompanhamento de entregas",
            LocalDate.now(),
            LocalDate.now().plusDays(90));

        p2.adicionarFuncionario(f2);
        p2.adicionarFuncionario(f3);

        Tarefa t6 = new Tarefa("Design de Telas",
            "Criar protótipo das telas principais",
            LocalDate.now(),
            LocalDate.now().plusDays(10));
        t6.setStatus(StatusTarefa.EM_ANDAMENTO);
        t6.setResponsavel(f2);

        p2.adicionarTarefa(t6);
        projetos.add(p2);
    }

    // Métodos de acesso
    public List<Projeto> getProjetos() { return new ArrayList<>(projetos); }
    public List<Funcionario> getFuncionarios() { return new ArrayList<>(funcionarios); }

    public void adicionarProjeto(Projeto projeto) {
        projetos.add(projeto);
    }

    public void adicionarFuncionario(Funcionario funcionario) {
        funcionarios.add(funcionario);
    }

    public Funcionario buscarFuncionarioPorNome(String nome) {
        return funcionarios.stream()
            .filter(f -> f.getNome().equalsIgnoreCase(nome))
            .findFirst()
            .orElse(null);
    }
}

// ========== INTERFACE GRÁFICA (SWING) ==========

public class SistemaGerenciamentoProjetos extends JFrame {
    private GerenciadorProjetos gerenciador;
    private JTabbedPane tabbedPane;
    private JComboBox<Projeto> comboProjetosVisao;
    private JComboBox<Funcionario> comboFuncionariosVisao;
    private JTable tabelaTarefas;
    private DefaultTableModel modeloTabelaTarefas;
    private JProgressBar progressoProjeto;
    private JLabel lblInfoProjeto;

    public SistemaGerenciamentoProjetos() {
        gerenciador = new GerenciadorProjetos();
        inicializarInterface();
    }

    private void inicializarInterface() {
        setTitle("Sistema de Gerenciamento de Projetos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Criar abas
        tabbedPane = new JTabbedPane();

        // Aba de Visão Geral do Projeto
        JPanel painelProjeto = criarPainelProjeto();
        tabbedPane.addTab("Projetos", painelProjeto);

        // Aba de Tarefas por Funcionário
        JPanel painelFuncionario = criarPainelFuncionario();
        tabbedPane.addTab("Funcionários", painelFuncionario);

        // Aba de Atribuição de Tarefas
        JPanel painelAtribuicao = criarPainelAtribuicao();
        tabbedPane.addTab("Atribuir Tarefas", painelAtribuicao);

        // Aba de Cadastros
        JPanel painelCadastros = criarPainelCadastros();
        tabbedPane.addTab("Cadastros", painelCadastros);

        add(tabbedPane, BorderLayout.CENTER);

        // Configurações da janela
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }

    private JPanel criarPainelProjeto() {
        JPanel painel = new JPanel(new BorderLayout());

        // Painel superior com seleção de projeto
        JPanel painelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        painelSuperior.add(new JLabel("Projeto:"));
        comboProjetosVisao = new JComboBox<>();
        atualizarComboProjetos(comboProjetosVisao);
        comboProjetosVisao.addActionListener(e -> atualizarVisaoProjeto());
        painelSuperior.add(comboProjetosVisao);

        progressoProjeto = new JProgressBar(0, 100);
        progressoProjeto.setStringPainted(true);
        progressoProjeto.setPreferredSize(new Dimension(200, 25));
        painelSuperior.add(Box.createHorizontalStrut(20));
        painelSuperior.add(new JLabel("Progresso:"));
        painelSuperior.add(progressoProjeto);

        painel.add(painelSuperior, BorderLayout.NORTH);

        // Painel central com informações do projeto
        JPanel painelCentral = new JPanel(new BorderLayout());
        painelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Informações do projeto
        lblInfoProjeto = new JLabel();
        lblInfoProjeto.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        painelCentral.add(lblInfoProjeto, BorderLayout.NORTH);

        // Tabela de tarefas
        String[] colunas = {"ID", "Título", "Status", "Responsável", "Prioridade", "Prazo"};
        modeloTabelaTarefas = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaTarefas = new JTable(modeloTabelaTarefas);
        tabelaTarefas.setRowHeight(25);
        tabelaTarefas.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabelaTarefas.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabelaTarefas.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabelaTarefas.getColumnModel().getColumn(3).setPreferredWidth(150);
        tabelaTarefas.getColumnModel().getColumn(4).setPreferredWidth(80);
        tabelaTarefas.getColumnModel().getColumn(5).setPreferredWidth(100);

        // Renderizador customizado para cores de status
        tabelaTarefas.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);

                String status = (String) value;
                if (status != null) {
                    if (status.equals(StatusTarefa.CONCLUIDA.getDescricao())) {
                        c.setForeground(new Color(0, 150, 0));
                    } else if (status.equals(StatusTarefa.ATRASADA.getDescricao())) {
                        c.setForeground(Color.RED);
                    } else if (status.equals(StatusTarefa.EM_ANDAMENTO.getDescricao())) {
                        c.setForeground(Color.BLUE);
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabelaTarefas);
        painelCentral.add(scrollPane, BorderLayout.CENTER);

        // Painel de estatísticas
        JPanel painelStats = new JPanel(new GridLayout(2, 3, 10, 5));
        painelStats.setBorder(BorderFactory.createTitledBorder("Estatísticas"));
        painelStats.setPreferredSize(new Dimension(0, 100));

        JLabel lblTotalTarefas = new JLabel("Total: 0");
        JLabel lblConcluidas = new JLabel("Concluídas: 0");
        JLabel lblEmAndamento = new JLabel("Em Andamento: 0");
        JLabel lblPendentes = new JLabel("Pendentes: 0");
        JLabel lblAtrasadas = new JLabel("Atrasadas: 0");
        JLabel lblEquipe = new JLabel("Equipe: 0 pessoas");

        painelStats.add(lblTotalTarefas);
        painelStats.add(lblConcluidas);
        painelStats.add(lblEmAndamento);
        painelStats.add(lblPendentes);
        painelStats.add(lblAtrasadas);
        painelStats.add(lblEquipe);

        painelCentral.add(painelStats, BorderLayout.SOUTH);

        painel.add(painelCentral, BorderLayout.CENTER);

        // Atualizar visão inicial
        if (comboProjetosVisao.getItemCount() > 0) {
            atualizarVisaoProjeto();
        }

        return painel;
    }

    private JPanel criarPainelFuncionario() {
        JPanel painel = new JPanel(new BorderLayout());

        // Painel superior
        JPanel painelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        painelSuperior.add(new JLabel("Funcionário:"));
        comboFuncionariosVisao = new JComboBox<>();
        atualizarComboFuncionarios(comboFuncionariosVisao);
        painelSuperior.add(comboFuncionariosVisao);

        JButton btnVisualizarTarefas = new JButton("Visualizar Tarefas");
        btnVisualizarTarefas.addActionListener(e -> visualizarTarefasFuncionario());
        painelSuperior.add(btnVisualizarTarefas);

        painel.add(painelSuperior, BorderLayout.NORTH);

        // Área de texto para mostrar tarefas
        JTextArea areaTarefas = new JTextArea();
        areaTarefas.setEditable(false);
        areaTarefas.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(areaTarefas);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Tarefas do Funcionário"));

        painel.add(scrollPane, BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarPainelAtribuicao() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Seleção de projeto
        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Projeto:"), gbc);

        gbc.gridx = 1;
        JComboBox<Projeto> comboProjeto = new JComboBox<>();
        atualizarComboProjetos(comboProjeto);
        painel.add(comboProjeto, gbc);

        // Seleção de tarefa
        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(new JLabel("Tarefa:"), gbc);

        gbc.gridx = 1;
        JComboBox<Tarefa> comboTarefa = new JComboBox<>();
        comboProjeto.addActionListener(e -> {
            comboTarefa.removeAllItems();
            Projeto projeto = (Projeto) comboProjeto.getSelectedItem();
            if (projeto != null) {
                for (Tarefa t : projeto.getTarefas()) {
                    comboTarefa.addItem(t);
                }
            }
        });
        painel.add(comboTarefa, gbc);

        // Seleção de funcionário
        gbc.gridx = 0; gbc.gridy = 2;
        painel.add(new JLabel("Funcionário:"), gbc);

        gbc.gridx = 1;
        JComboBox<Funcionario> comboFuncionario = new JComboBox<>();
        atualizarComboFuncionarios(comboFuncionario);
        painel.add(comboFuncionario, gbc);

        // Seleção de status
        gbc.gridx = 0; gbc.gridy = 3;
        painel.add(new JLabel("Status:"), gbc);

        gbc.gridx = 1;
        JComboBox<StatusTarefa> comboStatus = new JComboBox<>(StatusTarefa.values());
        painel.add(comboStatus, gbc);

        // Botão atribuir
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        JButton btnAtribuir = new JButton("Atribuir Tarefa");
        painel.add(btnAtribuir, gbc);

        btnAtribuir.addActionListener(e -> {
            Tarefa tarefa = (Tarefa) comboTarefa.getSelectedItem();
            Funcionario funcionario = (Funcionario) comboFuncionario.getSelectedItem();
            StatusTarefa status = (StatusTarefa) comboStatus.getSelectedItem();

            if (tarefa == null || funcionario == null || status == null) {
                JOptionPane.showMessageDialog(this,
                    "Selecione todas as opções!",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            tarefa.setResponsavel(funcionario);
            tarefa.setStatus(status);

            JOptionPane.showMessageDialog(this,
                "Tarefa atribuída com sucesso!",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);

            atualizarVisaoProjeto();
        });

        // Inicializar combos
        if (comboProjeto.getItemCount() > 0) {
            comboProjeto.setSelectedIndex(0);
            // Preenche as tarefas do primeiro projeto selecionado
            Projeto projeto = (Projeto) comboProjeto.getSelectedItem();
            if (projeto != null) {
                for (Tarefa t : projeto.getTarefas()) {
                    comboTarefa.addItem(t);
                }
            }
        }

        return painel;
    }

    private JPanel criarPainelCadastros() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Nome
        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1;
        JTextField campoNome = new JTextField(20);
        painel.add(campoNome, gbc);

        // Cargo
        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(new JLabel("Cargo:"), gbc);

        gbc.gridx = 1;
        JTextField campoCargo = new JTextField(20);
        painel.add(campoCargo, gbc);

        // Botão cadastrar
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        JButton btnCadastrar = new JButton("Cadastrar Funcionário");
        painel.add(btnCadastrar, gbc);

        btnCadastrar.addActionListener(e -> {
            String nome = campoNome.getText().trim();
            String cargo = campoCargo.getText().trim();

            if (nome.isEmpty() || cargo.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Preencha todos os campos!",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // cria funcionário sem email (mantendo campo existente no modelo)
            Funcionario novo = new Funcionario(nome, cargo, "");
            gerenciador.adicionarFuncionario(novo);

            // atualiza combo de funcionários na aba correspondente
            atualizarComboFuncionarios(comboFuncionariosVisao);

            JOptionPane.showMessageDialog(this,
                "Funcionário cadastrado com sucesso!",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);

            campoNome.setText("");
            campoCargo.setText("");
        });

        return painel;
    }

    private void atualizarComboProjetos(JComboBox<Projeto> combo) {
        combo.removeAllItems();
        for (Projeto projeto : gerenciador.getProjetos()) {
            combo.addItem(projeto);
        }
    }

    private void atualizarComboFuncionarios(JComboBox<Funcionario> combo) {
        combo.removeAllItems();
        for (Funcionario funcionario : gerenciador.getFuncionarios()) {
            combo.addItem(funcionario);
        }
    }

    private void atualizarVisaoProjeto() {
        Projeto projetoSelecionado = (Projeto) comboProjetosVisao.getSelectedItem();
        if (projetoSelecionado == null) return;

        // Atualizar informações do projeto
        StringBuilder info = new StringBuilder();
        info.append("<html><b>Projeto:</b> ").append(projetoSelecionado.getNome());
        info.append("<br><b>Descrição:</b> ").append(projetoSelecionado.getDescricao());
        info.append("<br><b>Período:</b> ");
        info.append(projetoSelecionado.getDataInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        info.append(" a ");
        info.append(projetoSelecionado.getDataFim().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        info.append("</html>");
        lblInfoProjeto.setText(info.toString());

        // Atualizar barra de progresso
        double progresso = projetoSelecionado.getProgressoPercentual();
        progressoProjeto.setValue((int) progresso);
        progressoProjeto.setString(String.format("%.1f%%", progresso));

        // Atualizar tabela de tarefas
        modeloTabelaTarefas.setRowCount(0);
        for (Tarefa tarefa : projetoSelecionado.getTarefas()) {
            Object[] linha = {
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getStatus().getDescricao(),
                tarefa.getResponsavel() != null ? tarefa.getResponsavel().getNome() : "Não atribuída",
                tarefa.getPrioridadeString(),
                tarefa.getDataPrazoFormatada()
            };
            modeloTabelaTarefas.addRow(linha);
        }

        // Atualizar estatísticas
        List<Tarefa> tarefas = projetoSelecionado.getTarefas();
        int total = tarefas.size();
        int concluidas = projetoSelecionado.getTarefasPorStatus(StatusTarefa.CONCLUIDA).size();
        int emAndamento = projetoSelecionado.getTarefasPorStatus(StatusTarefa.EM_ANDAMENTO).size();
        int pendentes = projetoSelecionado.getTarefasPorStatus(StatusTarefa.PENDENTE).size();
        int atrasadas = projetoSelecionado.getTarefasPorStatus(StatusTarefa.ATRASADA).size();
        int equipe = projetoSelecionado.getEquipe().size();

        // Localiza o painel de estatísticas (SOUTH do painel central)
        JPanel painelProjeto = (JPanel) tabbedPane.getComponentAt(0);
        JPanel painelCentral = (JPanel) painelProjeto.getComponent(1);
        JPanel painelStats = (JPanel) painelCentral.getComponent(2);
        Component[] componentes = painelStats.getComponents();

        if (componentes.length >= 6) {
            ((JLabel) componentes[0]).setText("Total: " + total);
            ((JLabel) componentes[1]).setText("Concluídas: " + concluidas);
            ((JLabel) componentes[2]).setText("Em Andamento: " + emAndamento);
            ((JLabel) componentes[3]).setText("Pendentes: " + pendentes);
            ((JLabel) componentes[4]).setText("Atrasadas: " + atrasadas);
            ((JLabel) componentes[5]).setText("Equipe: " + equipe + " pessoas");
        }
    }

    private void visualizarTarefasFuncionario() {
        Funcionario funcionario = (Funcionario) comboFuncionariosVisao.getSelectedItem();
        if (funcionario == null) return;

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("RELATÓRIO DE TAREFAS - ").append(funcionario.getNome().toUpperCase()).append("\n");
        relatorio.append("Cargo: ").append(funcionario.getCargo()).append("\n");
        relatorio.append("Email: ").append(funcionario.getEmail()).append("\n");
        relatorio.append("================================================================================\n\n");

        List<Tarefa> tarefas = funcionario.getTarefasAtribuidas();

        if (tarefas.isEmpty()) {
            relatorio.append("Nenhuma tarefa atribuída.\n");
        } else {
            relatorio.append("TOTAL DE TAREFAS: ").append(tarefas.size()).append("\n");
            relatorio.append("CONCLUÍDAS: ").append(funcionario.getTarefasConcluidas()).append("\n\n");

            // Agrupar tarefas por status
            for (StatusTarefa status : StatusTarefa.values()) {
                List<Tarefa> tarefasStatus = tarefas.stream()
                    .filter(t -> t.getStatus() == status)
                    .collect(Collectors.toList());

                if (!tarefasStatus.isEmpty()) {
                    relatorio.append("── ").append(status.getDescricao().toUpperCase()).append(" ──\n");
                    for (Tarefa tarefa : tarefasStatus) {
                        relatorio.append("• ").append(tarefa.getTitulo()).append("\n");
                        relatorio.append("  Prioridade: ").append(tarefa.getPrioridadeString());
                        relatorio.append(" | Prazo: ").append(tarefa.getDataPrazoFormatada()).append("\n");
                        relatorio.append("  Descrição: ").append(tarefa.getDescricao()).append("\n\n");
                    }
                }
            }
        }

        // Encontrar a área de texto no painel de funcionários
        JPanel painelFuncionarios = (JPanel) tabbedPane.getComponentAt(1);
        JScrollPane scrollPane = (JScrollPane) painelFuncionarios.getComponent(1);
        JTextArea areaTarefas = (JTextArea) scrollPane.getViewport().getView();
        areaTarefas.setText(relatorio.toString());
        areaTarefas.setCaretPosition(0);
    }

    public static void main(String[] args) {
        // Configurar look and feel do sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Executar na thread de eventos do Swing
        SwingUtilities.invokeLater(() -> {
            new SistemaGerenciamentoProjetos().setVisible(true);
        });
    }
}
