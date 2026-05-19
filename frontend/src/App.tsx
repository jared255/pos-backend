const API_BASE_URL = "http://localhost:8080";

export default function App() {
  return (
    <main className="container">
      <h1>POS Frontend</h1>
      <p>Base frontend para consumir el backend Spring Boot.</p>
      <p>
        API base configurada: <code>{API_BASE_URL}</code>
      </p>
    </main>
  );
}
