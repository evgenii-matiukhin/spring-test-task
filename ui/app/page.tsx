import { LoginForm } from "@/components/login-form";

export default function Home() {
  return (
    <main className="min-h-screen flex items-center justify-center bg-gradient-to-br from-background to-secondary/20 p-4">
      <div className="w-full max-w-md">
        <LoginForm />
      </div>
    </main>
  );
}