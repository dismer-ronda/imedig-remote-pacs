\NeedsTeXFormat{LaTeX2e}[1995/12/01]
\ProvidesClass{plantilla-manual-usuario}[2013/05/26 v1.0.0.0 Plantilla para manual de usuario.]
\ProcessOptions
\LoadClass[11pt,a4paper,oneside]{article}
\pdfoutput=1
\RequirePackage[utf8]{inputenc}
\RequirePackage[english,activeacute]{babel}
\RequirePackage{url}
\RequirePackage[pdftex]{graphicx}
\RequirePackage[absolute]{textpos}
\RequirePackage{listings}
\RequirePackage{fancyvrb}
\RequirePackage{color}
\RequirePackage{fancybox}
\RequirePackage{ifthen}
\RequirePackage{amssymb}
\RequirePackage{multibib}
\RequirePackage{eurosym}
\RequirePackage{titlesec}
\RequirePackage{setspace}
\RequirePackage[margin=2.5cm]{geometry}
\RequirePackage[spanish]{varioref}
\RequirePackage[a4paper=true]{hyperref}
\RequirePackage{anysize}
\RequirePackage{fancyhdr}
\RequirePackage[scaled]{helvet}
\RequirePackage{xspace}
\RequirePackage{lastpage}
\RequirePackage{titlesec}
\RequirePackage[titles]{tocloft} 

\geometry{left=2cm, right=2cm, top=2cm, bottom=3cm}

\addtolength{\footskip}{0.5cm}

\pagestyle{fancy} 

\fancyhead{}
\fancyfoot{}

\setlength{\columnsep}{7mm}
\setlength{\parindent}{0mm}

\addtolength{\parskip}{\baselineskip}

% Cambia el formato de los capitulos 
%\titleformat{\chapter}{\normalfont}{\Large\bfseries\filcenter\thechapter}{8pt}{\Large\bfseries\filcenter}

\newcommand{\frontmatter}
{
  \fancypagestyle{plain}
  {
    \fancyhead[l]{}
    \fancyhead[r]{}
    \fancyhead[c]{\includegraphics[width=2.5cm]{\@logotipo}} 
    \renewcommand{\headrulewidth}{0.0pt}
    \fancyfoot[l]{} 
    \fancyfoot[c]{}
    \fancyfoot[r]{} 
    \renewcommand{\footrulewidth}{0.0pt}
  }

  \fancyhead[l]{}
  \fancyhead[r]{}
  \fancyhead[c]{} 
  \renewcommand{\headrulewidth}{0.0pt}
  \fancyfoot[l]{} 
  \fancyfoot[c]{}
  \fancyfoot[r]{} 
  \renewcommand{\footrulewidth}{0.0pt}
}

\newcommand{\mainmatter}
{
  \pagenumbering{arabic}
  
  \fancypagestyle{plain}
  {
    \fancyhead[l]{}
    \fancyhead[r]{}
    \fancyhead[c]{} 
    \renewcommand{\headrulewidth}{0.0pt}
    \fancyfoot[l]{\includegraphics[width=2.5cm]{\@company}} 
    \fancyfoot[c]{\@title}
    \fancyfoot[r]{\thepage/\pageref{LastPage}} 
  }

  \fancyhead[l]{}
  \fancyhead[r]{}
  \fancyhead[c]{} 
  \renewcommand{\headrulewidth}{0.0pt}
  \fancyfoot[l]{\includegraphics[width=2.5cm]{\@company}} 
  \fancyfoot[c]{\@title}
  \fancyfoot[r]{\thepage/\pageref{LastPage}} 
  \renewcommand{\footrulewidth}{0.4pt}
}

\AtBeginDocument
{
}

\AtEndDocument
{
}

\renewcommand*\familydefault{\sfdefault} 

\newcommand{\autor}[1]{\def\@autor{#1}}

\newcommand{\proyecto}[1]{\def\@proyecto{#1}}
\newcommand{\logotipo}[1]{\def\@logotipo{#1}}
\newcommand{\resumen}[1]{\def\@resumen{#1}}
\newcommand{\company}[1]{\def\@company{#1}}

\def\maketitle
{
  \null
  
  \thispagestyle{empty}
  
  \vfill
    
    \begin{center}
   
      % Logotipo 
      \ifthenelse{\equal{\@logotipo}{}}
      {}
      {
	\begin{textblock*}{13cm}(4cm,2cm)
	 \includegraphics[width=13cm]{\@logotipo}
	\end{textblock*}
      }

      % Proyecto y titulo del documento
      \begin{textblock*}{14cm}(3.5cm,10cm)
	  {\noindent\sffamily\Large\bf\@proyecto{}}\vspace{2.5cm}\\
	  {\sffamily\huge\bf\@title{}}
      \end{textblock*}

      % Autor
      \begin{textblock*}{14cm}(3.5cm,16.5cm)
	  {\noindent\sffamily\Large\bf \@autor{}}
      \end{textblock*}

      % Compañia 
      \begin{textblock*}{5cm}(5cm,27.1cm)
	\includegraphics[width=5cm]{\@company}
	%{\sffamily\Large\bf \@company }
      \end{textblock*}

      % Fecha
      \begin{textblock*}{4cm}(12cm,27.8cm)
	{\sffamily\normalfont \@date{}}
      \end{textblock*}

    \end{center}
  
  \vfill
  
  \null
}

\definecolor{colViolet}{rgb}{0.5,0,0.5}
\definecolor{colNavy}{rgb}{0,0,0.5}

\definecolor{colYellow}{rgb}{1,1,0.8}
\definecolor{colWhite}{rgb}{1,1,1}
\definecolor{colKeys}{rgb}{0,0,1}
\definecolor{colIdentifier}{rgb}{0,0,0}
\definecolor{colComments}{rgb}{1,0,0}
\definecolor{colString}{rgb}{0,0.5,0}

\lstset
{
  basicstyle=\ttfamily\small,
  identifierstyle=\color{colIdentifier},
  keywordstyle=\color{colKeys},
  stringstyle=\color{colString},
  commentstyle=\color{colComments},
  columns=flexible,
  tabsize=4,
  frame=none,
  extendedchars=true,
  showspaces=false,
  showstringspaces=false,
  numbers=none,
  numberstyle=\tiny,
  breaklines=true,
  backgroundcolor=\color{colWhite},
  breakautoindent=true,
  captionpos=b
}

\lstnewenvironment{sourcecode}[1]
{
  \definecolor {background} {rgb} {1,1,1}
  \ifthenelse {\equal {#1} {}}{\def\@lang{bash}}{\def\@lang{#1}}
  \ifthenelse {\equal {#1} {java}}{\definecolor{background}{rgb}{1,1,0.8}}{}
  \ifthenelse {\equal {#1} {php}}{\definecolor{background}{rgb}{1,1,0.8}}{}
  \ifthenelse {\equal {#1} {sql}}{\definecolor{background}{rgb}{0.85,0.85,0.85}}{}
  \ifthenelse {\equal {#1} {c++}}{\definecolor{background}{rgb}{0.85,0.85,0.85}}{}
  \lstset
  {
    language=\@lang,
    basicstyle=\ttfamily\small,
    identifierstyle=\color{colIdentifier},
    keywordstyle=\color{colKeys},
    stringstyle=\color{colString},
    commentstyle=\color{colComments},
    columns=flexible,
    tabsize=3,
    frame=none,
    extendedchars=true,
    showspaces=false,
    showstringspaces=false,
    numbers=none,
    numberstyle=\tiny,
    breaklines=true,
    backgroundcolor=\color{background},
    breakautoindent=true,
    captionpos=b
  }
}
{
}

\lstnewenvironment{bash}
{
  \lstset
  {
    language=bash,
    basicstyle=\ttfamily\small,
    identifierstyle=\color{colIdentifier},
    keywordstyle=\color{colKeys},
    stringstyle=\color{colString},
    commentstyle=\color{colComments},
    columns=flexible,
    tabsize=3,
    frame=none,
    extendedchars=true,
    showspaces=false,
    showstringspaces=false,
    numbers=none,
    numberstyle=\tiny,
    breaklines=true,
    backgroundcolor=\color{colWhite},
    breakautoindent=true,
    captionpos=b
  }
}
{
}

\newlength{\anchoFigura}
\newlength{\anchoFloat}
\newcommand{\figura}[5]
{
  \setlength{\anchoFloat}{#1\textwidth}
  \addtolength{\anchoFloat}{-4\fboxsep}
  \setlength{\anchoFigura}{\anchoFloat}
  \begin{figure}[h]
    \begin{center}
        \begin{minipage}{\anchoFloat}
          \begin{center}
            \includegraphics[width=\anchoFigura,#5]{#2}
	    \caption{#3}
            \label{#4}
          \end{center}
        \end{minipage}
    \end{center}
  \end{figure}
}

\newcommand{\referencia}[1]{\vref{#1}}

% Definimos el nivel de detalle del índice: \subsection
\setcounter{tocdepth}{2}

\newcommand{\quotes}[1]{``#1''\xspace}
\newcommand{\italics}[1]{{\it{#1}}\xspace}

