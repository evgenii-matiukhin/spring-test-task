"use client"

import { useEffect, useState } from "react"
import { Plus, FileDown, FileUp } from "lucide-react"
import { toast } from "sonner"

import { Button } from "@/components/ui/button"
import { DataTable } from "@/components/sections/data-table"
import { columns } from "@/components/sections/columns"
import { Pagination } from "@/components/sections/pagination"
import { SectionDialog } from "@/components/sections/section-dialog"
import { ImportDialog } from "@/components/sections/import-dialog"
import { fetchSections } from "@/lib/api/sections"
import type { Section, SectionPage } from "@/lib/types"
import { startExport, checkExportStatus, downloadExportFile } from "@/lib/api/import-export"

export default function SectionsPage() {
  const [sectionPage, setSectionPage] = useState<SectionPage>({
    content: [],
    empty: true,
    first: true,
    last: true,
    number: 0,
    numberOfElements: 0,
    size: 10,
    totalElements: BigInt(0),
    totalPages: 0,
    sort: {}
  })
  const [isLoading, setIsLoading] = useState(true)
  const [openDialog, setOpenDialog] = useState(false)
  const [openImportDialog, setOpenImportDialog] = useState(false)
  const [selectedSection, setSelectedSection] = useState<Section | undefined>()

  useEffect(() => {
    loadSections()
  }, [])

  async function loadSections(page = 0, size = 10) {
    try {
      setIsLoading(true)
      const data = await fetchSections({ page, size })
      setSectionPage(data)
    } catch (error) {
      toast.error("Failed to load sections")
    } finally {
      setIsLoading(false)
    }
  }

  const handleEdit = (section: Section) => {
    setSelectedSection(section)
    setOpenDialog(true)
  }

  const handleCloseDialog = (open: boolean) => {
    setOpenDialog(open)
    if (!open) {
      setSelectedSection(undefined)
    }
  }

  const handlePageChange = (page: number) => {
    loadSections(page, sectionPage.size)
  }

  const handlePageSizeChange = (size: number) => {
    loadSections(0, size)
  }

  async function handleExport() {
    try {
      const jobId = await startExport()
      if (!jobId) throw new Error('No job ID received')
      
      toast.info("Export started")
      
      const interval = setInterval(async () => {
        const status = await checkExportStatus(jobId)
        if (status === "DONE") {
          clearInterval(interval)
          const blob = await downloadExportFile(jobId)
          const url = window.URL.createObjectURL(blob)
          const a = document.createElement('a')
          a.href = url
          a.download = 'sections-export.xls'
          document.body.appendChild(a)
          a.click()
          window.URL.revokeObjectURL(url)
          document.body.removeChild(a)
          toast.success("Export completed")
        } else if (status === "ERROR") {
          clearInterval(interval)
          toast.error("Export failed")
        }
      }, 1000)
    } catch (error) {
      toast.error("Failed to start export")
    }
  }

  return (
    <div className="container mx-auto py-10">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold">Sections</h1>
        <div className="flex gap-4">
          <Button onClick={() => setOpenImportDialog(true)}>
            <FileUp className="mr-2 h-4 w-4" />
            Import
          </Button>
          <Button onClick={handleExport}>
            <FileDown className="mr-2 h-4 w-4" />
            Export
          </Button>
          <Button onClick={() => setOpenDialog(true)}>
            <Plus className="mr-2 h-4 w-4" />
            Add Section
          </Button>
        </div>
      </div>

      <div className="space-y-4">
        <DataTable 
          columns={columns({ onEdit: handleEdit })} 
          data={sectionPage.content} 
        />
        
        <Pagination
          currentPage={sectionPage.number}
          pageSize={sectionPage.size}
          totalPages={sectionPage.totalPages}
          totalElements={sectionPage.totalElements}
          onPageChange={handlePageChange}
          onPageSizeChange={handlePageSizeChange}
        />
      </div>

      <SectionDialog
        open={openDialog}
        onOpenChange={handleCloseDialog}
        section={selectedSection}
        onSuccess={() => loadSections(sectionPage.number, sectionPage.size)}
      />

      <ImportDialog
        open={openImportDialog}
        onOpenChange={setOpenImportDialog}
        onSuccess={() => loadSections(sectionPage.number, sectionPage.size)}
      />
    </div>
  )
}