import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './styles.css';
import Navigator from "./components/Navbar";
import Main from './pages/Main';
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import Claims from "./pages/Claims";

import NotFound from "./pages/NotFound";

function App() {
  return (
    <BrowserRouter as="main">
      <Navigator />
      <Routes>
        <Route path="/" element={<Main />} />
        <Route path='/login' element={<Login />} />
        <Route path='/signup' element={<Signup />} />
        <Route path='/claims' element={<Claims />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
