import { TopNav } from "@/components/layout/top-nav"

export default function SectionsLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <div className="min-h-screen flex flex-col">
      <TopNav />
      <main className="flex-1">{children}</main>
    </div>
  )
}