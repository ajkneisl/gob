import { Grommet } from 'grommet';
import { MemoryRouter as Router, Routes, Route } from 'react-router-dom';
import styled from 'styled-components';
import { createGlobalStyle } from 'styled-components';
import Header from './components/Header';

const HomeStyle = styled.div`
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
`;

const Home = () => {
    return (
        <HomeStyle>
          <p>i love gobspeak.</p>
        </HomeStyle>
    );
};

const GlobalStyle = createGlobalStyle`
  h1 {
    font-family: "Roboto Flex", sans-serif;
  }

  p {
    font-family: "Roboto", sans-serif;
  }

  * {
    padding: 0;
    margin: 0;
  }

  #container {
    min-height: calc(100vh - 48px);
    position: relative;
  }

  footer {
    height: 48px;
    bottom: 0;
    position: absolute;
    width: 100%;
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
  }
`;

const theme = {
  global: {
    font: {
      family: 'Roboto',
      size: '18px',
      height: '20px'
    }, 
    colors: {
      background: "#1a191a",
      heading: "black",
      text: "whitesmoke"
    }
  }
}

export default function App() {
    return (
        <Grommet background="background" theme={theme}>
          <GlobalStyle />
            <div id="container">
                <Header />
                <main>
                    <Router>
                        <Routes>
                            <Route path="/" element={<Home />} />
                        </Routes>
                    </Router>
                </main>
                <footer>
                    <p>
                        gobspeak ©{new Date().getFullYear()} — support:
                        aj@ajkneisl.dev
                    </p>
                </footer>
            </div>
        </Grommet>
    );
}
