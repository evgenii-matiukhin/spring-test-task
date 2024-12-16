"use client"

import { useState } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import * as z from "zod"
import { toast } from "sonner"

import { Button } from "@/components/ui/button"
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import { createSection, updateSection } from "@/lib/api/sections"
import type { Section } from "@/lib/types"

const formSchema = z.object({
  name: z.string().min(1, "Name is required"),
  geologicalClasses: z.array(z.string()).min(1, "At least one geological class is required"),
})

interface SectionDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  section?: Section
  onSuccess: () => void
}

export function SectionDialog({
  open,
  onOpenChange,
  section,
  onSuccess,
}: SectionDialogProps) {
  const [isLoading, setIsLoading] = useState(false)

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      name: section?.name || "",
      geologicalClasses: section?.geologicalClasses.map(gc => gc.id) || [],
    },
  })

  async function onSubmit(values: z.infer<typeof formSchema>) {
    setIsLoading(true)
    try {
      if (section) {
        await updateSection(section.id, values)
        toast.success("Section updated")
      } else {
        await createSection(values)
        toast.success("Section created")
      }
      onSuccess()
      onOpenChange(false)
    } catch (error) {
      toast.error(section ? "Failed to update section" : "Failed to create section")
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{section ? "Edit Section" : "Add Section"}</DialogTitle>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="name"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Name</FormLabel>
                  <FormControl>
                    <Input placeholder="Enter section name" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            {/* Add geological classes selection here */}
            <Button type="submit" disabled={isLoading}>
              {section ? "Update" : "Create"}
            </Button>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  )
}