"use client"

import { useState } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { Plus, X } from "lucide-react"
import { toast } from "sonner"

import { Button } from "@/components/ui/button"
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
import { sectionFormSchema } from "@/lib/validations/section"
import type { Section, GeoClass } from "@/lib/types"
import type { SectionFormValues } from "@/lib/validations/section"

interface SectionFormProps {
  section?: Section
  onSuccess: () => void
  onCancel: () => void
}

export function SectionForm({ section, onSuccess, onCancel }: SectionFormProps) {
  const [isLoading, setIsLoading] = useState(false)
  
  const form = useForm<SectionFormValues>({
    resolver: zodResolver(sectionFormSchema),
    defaultValues: {
      name: section?.name || "",
      geologicalClasses: section?.geologicalClasses || [{ id: undefined, name: "", code: ""}],
    },
  })

  async function onSubmit(values: SectionFormValues) {
    if (values.geologicalClasses.some(gc => !gc.name || !gc.code)) {
      toast.error("All geological classes must have a name and code")
      return
    }

    setIsLoading(true)
    try {
      const data = {
        name: values.name,
        geologicalClasses: values.geologicalClasses,
      }

      if (section) {
        await updateSection(section.id, data)
        toast.success("Section updated")
      } else {
        await createSection(data)
        toast.success("Section created")
      }
      onSuccess()
    } catch (error) {
      toast.error(section ? "Failed to update section" : "Failed to create section")
    } finally {
      setIsLoading(false) 
    }
  }

  const addClass = () => {
    const currentClasses = form.getValues("geologicalClasses")
    form.setValue("geologicalClasses", [
      ...currentClasses,
      { id: undefined, name: "", code: ""}
    ])
  }

  const removeClass = (index: number) => {
    const currentClasses = form.getValues("geologicalClasses")
    form.setValue("geologicalClasses", currentClasses.filter((_, i) => i !== index))
  }

  const updateClass = (index: number, field: keyof GeoClass, value: string) => {
    const currentClasses = form.getValues("geologicalClasses")
    form.setValue("geologicalClasses", 
      currentClasses.map((gc, i) =>
        i === index ? { ...gc, [field]: value } : gc
      )
    )
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
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
        
        <div className="space-y-4">
          <FormLabel >Geological Classes</FormLabel>
          <div className="space-y-4">
            {form.watch("geologicalClasses").map((geoClass, index) => (
              <div key={index} className="flex gap-2">
                <Input
                  placeholder="Class name"
                  value={geoClass.name}
                  onChange={(e) => updateClass(index, "name", e.target.value)}
                  className="flex-1"
                />
                <Input
                  placeholder="Class code"
                  value={geoClass.code}
                  onChange={(e) => updateClass(index, "code", e.target.value)}
                  className="w-32"
                />
                <Button
                  type="button"
                  variant="ghost"
                  size="icon"
                  onClick={() => removeClass(index)}
                  disabled={form.watch("geologicalClasses").length === 1}
                >
                  <X className="h-4 w-4" />
                </Button>
              </div>
            ))}
          </div>
          <Button
            type="button"
            variant="outline"
            size="sm"
            onClick={addClass}
            className="w-full"
          >
            <Plus className="h-4 w-4 mr-2" />
            Add Class
          </Button>
        </div>

        <div className="flex justify-end gap-4">
          <Button type="button" variant="outline" onClick={onCancel}>
            Cancel
          </Button>
          <Button type="submit" disabled={isLoading || form.watch("geologicalClasses").length === 0}>
            {section ? "Update" : "Create"}
          </Button>
        </div>
      </form>
    </Form>
  )
}